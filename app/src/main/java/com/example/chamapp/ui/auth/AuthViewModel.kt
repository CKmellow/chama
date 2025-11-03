package com.example.chamapp.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.AuthResponse
import com.example.chamapp.api.LoginRequest
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.SignupRequest
import com.example.chamapp.util.Event
import com.example.chamapp.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<Event<Boolean>>()
    val loginResult: LiveData<Event<Boolean>> = _loginResult

    private val _signupResult = MutableLiveData<Event<Boolean>>()
    val signupResult: LiveData<Event<Boolean>> = _signupResult

    /**
     * Handles user login via API.
     */
    fun loginUser(email: String, password: String) {
        Log.d("AuthViewModel", "loginUser called with email: $email")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = RetrofitClient.instance.login(
                    LoginRequest(email = email, password = password)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.access_token != null) {
                            Log.d("AuthViewModel", "Login success. Token: ${body.access_token}")
                            sessionManager.saveAuthToken(body.access_token)
                            _loginResult.value = Event(true)
                        } else {
                            Log.e("AuthViewModel", "Login response missing token.")
                            _loginResult.value = Event(false)
                        }
                    } else {
                        Log.e("AuthViewModel", "Login failed: ${response.errorBody()?.string()}")
                        _loginResult.value = Event(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _loginResult.value = Event(false)
                }
            }
        }
    }

    /**
     * Handles user registration via API.
     */
    fun registerUser(firstName: String, lastName: String, email: String, phoneNumber: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = RetrofitClient.instance.signup(
                    SignupRequest(
                        first_name = firstName,
                        last_name = lastName,
                        email = email,
                        phone_number = phoneNumber,
                        password = password
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("AuthViewModel", "Signup successful.")
                        _signupResult.value = Event(true)
                    } else {
                        Log.e("AuthViewModel", "Signup failed: ${response.errorBody()?.string()}")
                        _signupResult.value = Event(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _signupResult.value = Event(false)
                }
            }
        }
    }
}
