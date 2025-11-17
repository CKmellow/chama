package com.example.chamapp.ui.chama

import android.util.Log
import androidx.lifecycle.LiveData // <-- ADD THIS LINE
import androidx.lifecycle.MutableLiveData // <-- ADD THIS LINE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.CreateChamaRequest
import com.example.chamapp.api.GenericResponse
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.util.Event
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ChamaViewModel : ViewModel() {
    private val _chamas = MutableLiveData<List<com.example.chamapp.api.Chama>>()
    val chamas: LiveData<List<com.example.chamapp.api.Chama>> get() = _chamas

    fun fetchChamas() {
        viewModelScope.launch {
            try {
                val response = com.example.chamapp.api.RetrofitClient.instance.getChamas()
                if (response.isSuccessful) {
                    val chamas = response.body()?.chamas ?: emptyList()
                    _chamas.postValue(chamas)
                } else {
                    android.util.Log.e("ChamaViewModel", "getChamas API error: ${response.code()} - ${response.errorBody()?.string()}")
                    _chamas.postValue(emptyList())
                }
            } catch (e: Exception) {
                android.util.Log.e("ChamaViewModel", "Exception in getChamas: ${e.message}", e)
                _chamas.postValue(emptyList())
            }
        }
    }

    private val _createResult = MutableLiveData<Event<Pair<Boolean, String>>>()
    val createResult: LiveData<Event<Pair<Boolean, String>>> = _createResult

    fun createChama(request: CreateChamaRequest) {
    android.util.Log.d("ChamaViewModel", "createChama called with request: $request")
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.createChama(request)
                android.util.Log.d("ChamaViewModel", "Raw response: $response")
                if (response.isSuccessful) {
                    val successMsg = response.body()?.message ?: "Chama created successfully!"
                    android.util.Log.d("ChamaViewModel", "Parsed success message: $successMsg")
                    _createResult.postValue(Event(Pair(true, successMsg)))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ChamaViewModel", "API Error: HTTP ${response.code()} - $errorBody")
                    android.util.Log.e("ChamaViewModel", "Raw error body: $errorBody")
                    val errorMsg = try {
                        val parsedError = Gson().fromJson(errorBody, GenericResponse::class.java)
                        if (!parsedError.error.isNullOrEmpty()) {
                            parsedError.error
                        } else if (!parsedError.message.isNullOrEmpty()) {
                            parsedError.message
                        } else {
                            "An unknown error occurred. Please check the logs."
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ChamaViewModel", "Exception while parsing error: ${e.message}", e)
                        "Error ${response.code()}: Failed to create chama. Please check inputs and try again."
                    }
                    _createResult.postValue(Event(Pair(false, errorMsg)))
                }
            } catch (e: Exception) {
                Log.e(
                    "ChamaViewModel",
                    "Network/Exception: Create Chama failed",
                    e
                )
                android.util.Log.e("ChamaViewModel", "Exception: ${e.message}", e)
                _createResult.postValue(
                    Event(
                        Pair(
                            false,
                            "Network connection failed or server is unavailable."
                        )
                    )
                )
            }
        }
    }
}
