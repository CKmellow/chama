package com.example.chamapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.SignupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    fun registerUser(firstName: String, lastName: String, email: String, phoneNumber: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.signup(
                    SignupRequest(
                        first_name = firstName,
                        last_name = lastName,
                        email = email,
                        phone_number = phoneNumber,
                        password = password
                    )
                ).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("AuthViewModel", "Signup success: ${body?.message}")
                    } else {
                        Log.e("AuthViewModel", "Signup failed: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup error: ${e.message}")
            }
        }
    }
}
