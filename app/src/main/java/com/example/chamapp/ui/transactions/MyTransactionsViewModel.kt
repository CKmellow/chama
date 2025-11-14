package com.example.chamapp.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.App
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.Transaction
import com.example.chamapp.util.SessionManager
import kotlinx.coroutines.launch

class MyTransactionsViewModel : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val sessionManager = SessionManager(App.appContext)

    fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val userId = sessionManager.getUserId()

            if (userId == null) {
                _error.value = "User not logged in. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                val response = RetrofitClient.instance.getUserTransactions(userId)
                if (response.isSuccessful) {
                    _transactions.postValue(response.body()?.transactions ?: emptyList())
                } else {
                    _error.postValue("Failed to fetch transactions: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
