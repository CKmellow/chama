package com.example.chamapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.Chama
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _chamas = MutableLiveData<List<Chama>>()
    val chamas: LiveData<List<Chama>> = _chamas

    init {
        _chamas.observeForever {
            Log.d("HomeViewModel", "chamas LiveData updated: $it")
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchChamas(token: String?) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching chamas with token: $token")
                val response = RetrofitClient.instance.getChamas()
                Log.d("HomeViewModel", "Chamas response: ${response.body()} | Raw: ${response.raw()} | Error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    val chamasList = response.body()?.chamas ?: emptyList()
                    Log.d("HomeViewModel", "Parsed chamas: $chamasList")
                    _chamas.postValue(chamasList)
                    Log.d("HomeViewModel", "_chamas.postValue called with: $chamasList")
                    _error.postValue(null)
                } else {
                    val errorMsg = "Failed to fetch chamas: ${response.errorBody()?.string()} | Code: ${response.code()}"
                    Log.e("HomeViewModel", errorMsg)
                    _chamas.postValue(emptyList())
                    Log.d("HomeViewModel", "_chamas.postValue called with: emptyList (error)")
                    _error.postValue(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}", e)
                _chamas.postValue(emptyList())
                Log.d("HomeViewModel", "_chamas.postValue called with: emptyList (exception)")
                _error.postValue(e.message ?: "Unknown error")
            }
        }
    }
    
    fun fetchChamaDetails(chamaId: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching chama details for id: $chamaId")
                val response = RetrofitClient.instance.getChamaDetails(chamaId)
                if (response.isSuccessful) {
                    val chamaDetails = response.body()?.chama
                    Log.d("HomeViewModel", "Chama details fetched: ${chamaDetails?.chama_name}")
                    Log.d("HomeViewModel", "Chama members count from /fetch/:id: ${chamaDetails?.members?.size ?: 0}")
                    Log.d("HomeViewModel", "Chama raw chama_members count: ${chamaDetails?.chama_members?.size ?: 0}")
                    
                    // Update the chama in the list with full details
                    val currentChamas = _chamas.value?.toMutableList() ?: mutableListOf()
                    val index = currentChamas.indexOfFirst { it.id == chamaId }
                    if (index >= 0 && chamaDetails != null) {
                        val existingChama = currentChamas[index]
                        // Preserve enriched members from /fetch/chamas if they exist
                        // Only use /fetch/:id members if we don't have enriched members
                        val enrichedMembers = existingChama.members
                        val shouldPreserveMembers = !enrichedMembers.isNullOrEmpty() && 
                            enrichedMembers.any { !it.firstName.isNullOrBlank() || !it.lastName.isNullOrBlank() || !it.email.isNullOrBlank() }
                        
                        if (shouldPreserveMembers) {
                            Log.d("HomeViewModel", "Preserving ${enrichedMembers.size} enriched members from /fetch/chamas")
                            // Merge: keep enriched members, update other chama fields
                            currentChamas[index] = chamaDetails.copy(members = enrichedMembers)
                        } else {
                            Log.d("HomeViewModel", "No enriched members found, using members from /fetch/:id")
                            currentChamas[index] = chamaDetails
                        }
                        _chamas.postValue(currentChamas)
                        Log.d("HomeViewModel", "Updated chama in list. Final members count: ${currentChamas[index].members?.size ?: 0}")
                    } else if (chamaDetails != null) {
                        // Chama not in list, add it
                        currentChamas.add(chamaDetails)
                        _chamas.postValue(currentChamas)
                        Log.d("HomeViewModel", "Added new chama to list")
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch chama details: ${response.code()}")
                    Log.e("HomeViewModel", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching chama details: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

}
