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
                // Use the token from SessionManager, not the argument
                val response = RetrofitClient.instance.getChamas()
                Log.d("HomeViewModel", "Chamas response: ${response.body()} | Raw: ${response.raw()} | Error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    val chamasList = response.body()?.chamas ?: emptyList()
                    Log.d("HomeViewModel", "Parsed chamas: $chamasList")
                    _chamas.postValue(chamasList)
                    _error.postValue(null)
                } else {
                    val errorMsg = "Failed to fetch chamas: ${response.errorBody()?.string()} | Code: ${response.code()}"
                    Log.e("HomeViewModel", errorMsg)
                    _chamas.postValue(emptyList())
                    _error.postValue(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}", e)
                _chamas.postValue(emptyList())
                _error.postValue(e.message ?: "Unknown error")
            }
        }
    }

}
