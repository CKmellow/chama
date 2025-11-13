
package com.example.chamapp.ui.chama

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.Chama
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.UpdateMemberDetailsRequest
import kotlinx.coroutines.launch

class ChamaDetailsViewModel : ViewModel() {
    private val _chama = MutableLiveData<Chama?>()
    val chama: LiveData<Chama?> = _chama
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    private val _updateResult = MutableLiveData<String?>()
    val updateResult: LiveData<String?> = _updateResult

    fun fetchChamaDetails(chamaId: String) {
        android.util.Log.d("ChamaDetailsViewModel", "fetchChamaDetails called with chamaId: $chamaId (using getChamas)")
        viewModelScope.launch {
            try {
<<<<<<< Updated upstream
                val response = RetrofitClient.instance.getChamaDetails(chamaId)
                android.util.Log.d("ChamaDetailsViewModel", "API response: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}, errorBody=${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    _chama.postValue(response.body()?.chama)
                } else {
                    _error.postValue("Failed to load chama details. Code: ${response.code()} Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    fun updateMemberDetails(chamaId: String, userId: String, contributionAmount: Double?, role: String?) {
        viewModelScope.launch {
            try {
                val request = UpdateMemberDetailsRequest(
                    chama_id = chamaId,
                    user_id = userId,
                    contribution_amount = contributionAmount,
                    role = role
                )
                val response = RetrofitClient.instance.updateMemberDetails(request)
                if (response.isSuccessful) {
                    _updateResult.postValue(response.body()?.message ?: "Update successful!")
                } else {
                    _updateResult.postValue("Failed to update details.")
                }
            } catch (e: Exception) {
                _updateResult.postValue("Network error: ${e.message}")
=======
                val response = RetrofitClient.instance.getChamas()
                android.util.Log.d("ChamaDetailsViewModel", "getChamas response: $response")
                if (response.isSuccessful) {
                    val chamas = response.body()?.chamas ?: emptyList()
                    android.util.Log.d("ChamaDetailsViewModel", "Fetched chamas: $chamas")
                    val chama = chamas.find { it.id == chamaId }
                    android.util.Log.d("ChamaDetailsViewModel", "Selected chama: $chama")
                    _chamaDetails.postValue(chama)
                } else {
                    android.util.Log.e("ChamaDetailsViewModel", "Failed to fetch chamas: ${response.message()}")
                    _errorMessage.postValue("Failed to fetch chamas: ${response.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChamaDetailsViewModel", "Exception: ${e.message}")
                _errorMessage.postValue("An error occurred: ${e.message}")
>>>>>>> Stashed changes
            }
        }
    }
}
