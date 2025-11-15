package com.example.chamapp.api

import com.example.chamapp.App
import com.example.chamapp.util.SessionManager
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PATCH
// =====================
// DATA CLASSES
// =====================

data class UpdateMemberDetailsRequest(
    val chama_id: String,
    val user_id: String,
    val contribution_amount: Double?,
    val role: String?
)
// DATA CLASSES
// =====================

// --- AUTH REQUESTS & RESPONSES ---
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

// --- CHAMA DATA CLASSES ---
data class ChamaMemberRelation(
    @SerializedName("id") val id: String,
    @SerializedName("chama_id") val chamaId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("role") val role: String?,
    @SerializedName("contribution_amount") val contributionAmount: Double?,
    @SerializedName("joined_at") val joinedAt: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null
)
data class Chama(
    @SerializedName("chama_id") val id: String,
    @SerializedName("chama_name") val chama_name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("chama_type") val chama_type: String?,
    @SerializedName("invitation_code") val invitation_code: String?,
    @SerializedName("is_invitation_code_active") val is_invitation_code_active: Boolean?,
    @SerializedName("monthly_contribution_amount") val monthly_contribution_amount: Double?,
    @SerializedName("contribution_frequency") val contribution_frequency: String?,
    @SerializedName("contribution_due_day") val contribution_due_day: String?,
    @SerializedName("loan_interest_rate") val loan_interest_rate: Double?,
    @SerializedName("max_loan_multiplier") val max_loan_multiplier: Double?,
    @SerializedName("loan_max_term_months") val loan_max_term_months: Int?,
    @SerializedName("meeting_frequency") val meeting_frequency: String?,
    @SerializedName("meeting_day") val meeting_day: String?,
    @SerializedName("created_by") val created_by: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?,
    @SerializedName("is_active") val is_active: Boolean?,
    @SerializedName("total_balance") val total_balance: Double?,
    val role: String? = null,
    val myContributions: Double? = null,
    val totalBalance: Double? = null,
    val status: String? = null,
    val statusColor: String? = null,
    val nextMeeting: String? = null,
    @SerializedName("members") val members: List<ChamaMemberRelation>? = null
)

data class ChamasResponse(
    @SerializedName("chamas")
    val chamas: List<Chama>
)

data class CreateChamaRequest(
    val chama_name: String,
    val description: String,
    val chama_type: String,
    @SerializedName("monthly_contribution_amount") val contribution_amount: Double,
    @SerializedName("contribution_frequency") val contribution_schedule: String,
    @SerializedName("loan_interest_rate") val interest_rate: Double,
    @SerializedName("max_loan_multiplier") val max_loan_multiple: Double, // changed to Double
    @SerializedName("contribution_due_day") val contribution_due_day: String, // changed to String
    @SerializedName("loan_max_term_months") val loan_max_term_months: Int?,
    @SerializedName("meeting_frequency") val meeting_frequency: String,
    @SerializedName("meeting_day") val meeting_day: String
)

data class ChamaResponse(
    val message: String,
    val chama: Chama
)

data class GenericResponse(
    val message: String?,
    val error: String?
)

data class JoinChamaRequest(
    val invitation_code: String,
    val contribution_amount: Double? = null
)

data class JoinChamaResponse(
    val message: String?,
    val member: ChamaMemberRelation?,
    val error: String? = null
)
// =====================
// API SERVICE
// =====================
interface ApiService {
    // --- USERS ---
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserData>

    // --- AUTH ---
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    // --- CHAMAS ---
    @GET("chamas/fetch/chamas")
    suspend fun getChamas(): Response<ChamasResponse>

    @GET("chamas/fetch/{id}")
    suspend fun getChamaDetails(@Path("id") chamaId: String): Response<ChamaResponse>

    @POST("chamas/create")
    suspend fun createChama(@Body request: CreateChamaRequest): Response<ChamaResponse>
    @PATCH("chama_member/{id}")
    suspend fun updateMemberDetails(
        @Path("id") memberId: String,
        @Body request: UpdateMemberDetailsRequest
    ): Response<GenericResponse>

    @POST("chamas/join")
    suspend fun joinChama(@Body request: JoinChamaRequest): Response<JoinChamaResponse>
}

// =====================
// RETROFIT CLIENT
// =====================
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:4000/api/"
//private const val BASE_URL = "http://192.168.100.115:4000/api/"


    private val sessionManager by lazy { SessionManager(App.appContext) }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest = chain.request()
            val token = sessionManager.getAuthToken()
            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")

            if (!token.isNullOrEmpty()) {
                android.util.Log.d("RetrofitClient", "Attaching token: $token")
                requestBuilder.header("Authorization", "Bearer $token")
            } else {
                android.util.Log.d("RetrofitClient", "No token found, not attaching Authorization header.")
            }

            chain.proceed(requestBuilder.build())
        })
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}

object ApiHelper {
    suspend fun joinChama(invitationCode: String, contributionAmount: Double? = null): JoinChamaResult {
        return try {
            val response = RetrofitClient.instance.joinChama(JoinChamaRequest(invitationCode, contributionAmount))
            if (response.isSuccessful && response.body()?.member != null) {
                JoinChamaResult(success = true, error = null)
            } else {
                JoinChamaResult(success = false, error = response.body()?.error ?: response.message())
            }
        } catch (e: Exception) {
            JoinChamaResult(success = false, error = e.message)
        }
    }
}

data class JoinChamaResult(val success: Boolean, val error: String?)
// =====================
