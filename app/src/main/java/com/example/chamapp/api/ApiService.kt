package com.example.chamapp.api

// Corrected and necessary imports
import com.example.chamapp.App
import com.example.chamapp.util.SessionManager
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data Classes (No Changes Here, Assuming they are correct) ---

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
data class CreateChamaRequest(
    val chama_name: String,
    val description: String,
    val chama_type: String,
    @SerializedName("monthly_contribution_amount")
    val contribution_amount: Double,
    @SerializedName("contribution_frequency")
    val contribution_schedule: String,
    @SerializedName("loan_interest_rate")
    val interest_rate: Double,
    @SerializedName("max_loan_multiplier")
    val max_loan_multiple: Int,
    @SerializedName("contribution_due_day")
    val contribution_due_day: Int?,
    @SerializedName("loan_max_term_months")
    val loan_max_term_months: Int?,
    @SerializedName("meeting_frequency")
    val meeting_frequency: String,
    @SerializedName("meeting_day")
    val meeting_day: String
)

data class ChamaResponse(val message: String, val chama: Chama)
data class Chama(val id: String, val chama_name: String, val invitation_code: String)
data class GenericResponse(val message: String?, val error: String?)

// --- ApiService Interface ---
interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>
    @POST("chamas/create")
    suspend fun createChama(@Body request: CreateChamaRequest): Response<ChamaResponse> // Removed stray '}'
}

// --- CORRECTED RetrofitClient ---
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:4000/api/"

    private val sessionManager by lazy { SessionManager(App.appContext) }

    // This client will add the auth token to every request
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val token = sessionManager.getAuthToken()
            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }.build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // 👇 THIS IS THE CRITICAL FIX: Use the client with the interceptor
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}
