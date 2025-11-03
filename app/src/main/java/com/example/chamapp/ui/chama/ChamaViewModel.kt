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

    private val _createResult = MutableLiveData<Event<Pair<Boolean, String>>>()
    val createResult: LiveData<Event<Pair<Boolean, String>>> = _createResult

    fun createChama(request: CreateChamaRequest) {
        android.util.Log.d("ChamaViewModel", "createChama called with request: $request")
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.createChama(request)

                if (response.isSuccessful) {
                    val successMsg = response.body()?.message ?: "Chama created successfully!"
                    _createResult.postValue(Event(Pair(true, successMsg)))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ChamaViewModel", "API Error: HTTP ${response.code()} - $errorBody")

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
