package com.example.chamapp.ui.chama

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.Chama
import com.example.chamapp.api.RetrofitClient
import kotlinx.coroutines.launch

class ChamaDetailsViewModel : ViewModel() {

    private val _chamaDetails = MutableLiveData<Chama?>()
    val chamaDetails: LiveData<Chama?> = _chamaDetails

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchChamaDetails(chamaId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaDetails(chamaId)
                if (response.isSuccessful) {
                    _chamaDetails.postValue(response.body()?.chama)
                } else {
                    _errorMessage.postValue("Failed to fetch details: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.message}")
            }
        }
    }
}
