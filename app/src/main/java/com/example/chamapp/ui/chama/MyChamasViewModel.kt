package com.example.chamapp.ui.chama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.data.Chama
import com.example.chamapp.data.ChamaMemberRelation
import kotlinx.coroutines.launch

class MyChamasViewModel : ViewModel() {
    private val _chamas = MutableLiveData<List<Chama>>()
    val chamas: LiveData<List<Chama>> get() = _chamas
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchChamas() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getChamas()
                if (response.isSuccessful) {
                    val chamasList = response.body()?.chamas ?: emptyList()
                    _chamas.value = chamasList.map { apiChama ->
                        Chama(
                            id = apiChama.id,
                            name = apiChama.chama_name,
                            role = apiChama.role,
                            myContributions = apiChama.myContributions?.toString(),
                            totalBalance = apiChama.totalBalance?.toString(),
                            status = apiChama.status,
                            statusColor = apiChama.statusColor,
                            nextMeeting = apiChama.nextMeeting,
                            members = apiChama.members?.map { member ->
                                ChamaMemberRelation(
                                    id = member.id,
                                    userId = member.userId,
                                    name = member.name, // Use 'name' from API
                                    role = member.role,
                                    status = member.status,
                                    email = member.email,
                                    phoneNumber = member.phoneNumber?.toString(), // Convert to String
                                    joinedAt = member.joinedAt
                                )
                            }
                        )
                    }
                    _loading.value = false
                } else {
                    _error.value = "Failed to load chamas: ${response.message()}"
                    _chamas.value = emptyList()
                    _loading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                _chamas.value = emptyList()
                _loading.value = false
            }
        }
    }
}
