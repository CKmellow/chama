package com.example.chamapp.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * A more robust Chama data class where fields that might be null in the database are explicitly marked as nullable.
 * This prevents parsing errors if the server sends an incomplete object.
 */
data class Chama(
    @SerializedName("chama_id")
    val id: Int, // Primary key, should not be null
    @SerializedName("chama_name")
    val chama_name: String, // Chama name is essential, should not be null
    @SerializedName("description")
    val description: String?, // Can be null
    @SerializedName("chama_type")
    val chama_type: String?, // Can be null
    @SerializedName("invitation_code")
    val invitation_code: String?,
    @SerializedName("is_invitation_code_active")
    val is_invitation_code_active: Boolean?,
    @SerializedName("monthly_contribution_amount")
    val monthly_contribution_amount: Double?, // Can be null
    @SerializedName("contribution_frequency")
    val contribution_frequency: String?,
    @SerializedName("contribution_due_day")
    val contribution_due_day: String?,
    @SerializedName("loan_interest_rate")
    val loan_interest_rate: Double?,
    @SerializedName("max_loan_multiplier")
    val max_loan_multiplier: Double?,
    @SerializedName("loan_max_term_months")
    val loan_max_term_months: Int?,
    @SerializedName("meeting_frequency")
    val meeting_frequency: String?,
    @SerializedName("meeting_day")
    val meeting_day: String?,
    @SerializedName("created_by")
    val created_by: String?,
    @SerializedName("created_at")
    val created_at: String?,
    @SerializedName("updated_at")
    val updated_at: String?,
    @SerializedName("is_active")
    val is_active: Boolean?,
    @SerializedName("total_balance")
    val total_balance: Double?,
    // The following fields are from the pivot table, they might not exist if the user is not a member of the chama
    val role: String? = null,
    val myContributions: Double? = null,
    val totalBalance: Double? = null,
    val status: String? = null,
    val statusColor: String? = null,
    val nextMeeting: String? = null
)

data class ChamasResponse(
    @SerializedName("chamas")
    val chamas: List<Chama>
)

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
    suspend fun signup(@Body body: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("chamas/fetch/chamas")
    suspend fun getChamas(): Response<ChamasResponse>

    @GET("chamas/fetch/{id}")
    suspend fun getChamaDetails(@Path("id") chamaId: String): Response<Chama>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:4000/api/"

    // Add an OkHttpClient with an interceptor to add the Authorization header if a token is present
    private fun getClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (!token.isNullOrEmpty()) {
                addInterceptor(Interceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                })
            }
        }.build()
    }

    fun getInstance(token: String?): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient(token))
            .build()
            .create(ApiService::class.java)
    }
}