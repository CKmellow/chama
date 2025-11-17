package com.example.chamapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.AuthResponse
import com.example.chamapp.api.LoginRequest
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.SignupRequest
import com.example.chamapp.data.AuthResult
import com.example.chamapp.util.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<AuthResult<AuthResponse>>()
    val loginResult: LiveData<AuthResult<AuthResponse>> = _loginResult

    private val _registrationResult = MutableLiveData<AuthResult<AuthResponse>>()
    val registrationResult: LiveData<AuthResult<AuthResponse>> = _registrationResult

    fun login(email: String, password: String) {
        _loginResult.value = AuthResult.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.value = AuthResult.Success(response.body()!!)
                    saveUserDetails(response.body()!!)
                } else {
                    _loginResult.value = AuthResult.Error("Login failed")
                }
            } catch (e: Exception) {
                _loginResult.value = AuthResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun registerUser(firstName: String, lastName: String, email: String, phoneNumber: String, password: String) {
        _registrationResult.value = AuthResult.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.signup(SignupRequest(firstName, lastName, email, phoneNumber, password))
                if (response.isSuccessful) {
                    _registrationResult.value = AuthResult.Success(response.body()!!)
                } else {
                    _registrationResult.value = AuthResult.Error("Registration failed")
                }
            } catch (e: Exception) {
                _registrationResult.value = AuthResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    private fun saveUserDetails(authResponse: AuthResponse) {
        authResponse.access_token?.let {
            sessionManager.saveAuthToken(it)
        }
        authResponse.user?.let { user ->
            // Ensure SessionManager has a saveUserDetails method
            sessionManager.saveUserDetails(
                user.first_name ?: "",
                user.last_name ?: "",
                user.email
            )
        }
    }
}
