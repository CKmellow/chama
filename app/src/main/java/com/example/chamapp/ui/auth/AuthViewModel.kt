package com.example.chamapp.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.LoginRequest
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.SignupRequest
import com.example.chamapp.util.Event
import com.example.chamapp.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response // The critical import that resolves the errors

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<Event<Boolean>>()
    val loginResult: LiveData<Event<Boolean>> = _loginResult

    fun loginUser(email: String, password: String) {
    Log.d("AuthViewModel", "loginUser called with email: $email, password: $password")
    viewModelScope.launch {
            try {
                // This 'response' variable is now correctly typed as retrofit2.Response
                val response: Response<com.example.chamapp.api.AuthResponse> = RetrofitClient.instance.login(
                    LoginRequest(email = email, password = password)
                )

                // Now, these properties will be resolvzed correctly
                if (response.isSuccessful) {
                    val responseBody = response.body()

                        if (responseBody?.access_token != null) {
                            Log.d("AuthViewModel", "Received token from backend: ${responseBody.access_token}")
                            sessionManager.saveAuthToken(responseBody.access_token)
                            val savedToken = sessionManager.getAuthToken()
                            Log.d("AuthViewModel", "Token saved to SharedPreferences: $savedToken")
                            _loginResult.postValue(Event(true))
                        } else {
                            Log.e("AuthViewModel", "Login successful but no token in response.")
                            _loginResult.postValue(Event(false))
                        }
                } else {
                    val errorDetails = response.errorBody()?.string()
                    Log.e("AuthViewModel", "Login failed: $errorDetails")
                    _loginResult.postValue(Event(false))
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login network error: ${e.message}", e)
                _loginResult.postValue(Event(false))
            }
        }
    }

    fun registerUser(signupRequest: SignupRequest) {
        viewModelScope.launch {
            try {
                val response: Response<com.example.chamapp.api.AuthResponse> = RetrofitClient.instance.signup(signupRequest)
                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "Signup successful.")
                } else {
                    val errorDetails = response.errorBody()?.string()
                    Log.e("AuthViewModel", "Signup failed: $errorDetails")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup network error: ${e.message}", e)
            }
        }
    }
}
