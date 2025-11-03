package com.example.chamapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class SignupRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val password: String,
    val role: String = "user"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val access_token: String?,
    val user: UserData?
)

data class UserData(
    val id: String,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val phone_number: String?,
    val role: String?
)

interface ApiService {
    @POST("auth/signup")
    fun signup(@Body body: SignupRequest): Call<AuthResponse>

    @POST("auth/login")
    fun login(@Body body: LoginRequest): Call<AuthResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:4000/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

