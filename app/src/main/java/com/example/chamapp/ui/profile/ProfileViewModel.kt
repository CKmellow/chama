package com.example.chamapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.ApiService
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.UpdateProfileRequest
import com.example.chamapp.api.UserData
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<UserData?>()
    val user: LiveData<UserData?> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val apiService: ApiService = RetrofitClient.instance

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful) {
                    _user.postValue(response.body()?.user)
                } else {
                    _error.postValue("Failed to fetch profile: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred: ${e.message}")
            }
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    _user.postValue(response.body()?.user)
                    _updateSuccess.postValue(true)
                } else {
                    _error.postValue("Failed to update profile: ${response.message()}")
                    _updateSuccess.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred: ${e.message}")
                _updateSuccess.postValue(false)
            }
        }
    }
}
