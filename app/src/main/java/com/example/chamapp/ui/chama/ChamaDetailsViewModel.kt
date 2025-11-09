
package com.example.chamapp.ui.chama

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chamapp.api.Chama
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.UpdateMemberDetailsRequest
import com.example.chamapp.api.UpdateChamaMemberRequest
import com.example.chamapp.api.UpdateChamaMemberResponse
import com.example.chamapp.api.ChamaMember
import kotlinx.coroutines.launch

class ChamaDetailsViewModel : ViewModel() {
    fun updateMyMembership(memberId: String, role: String?, contributionAmount: Double?) {
        viewModelScope.launch {
            try {
                val request = com.example.chamapp.api.UpdateChamaMemberRequest(role, contributionAmount)
                android.util.Log.d("ChamaDetailsViewModel", "Sending updateMyMembership request: $request for memberId=$memberId")
                val response = com.example.chamapp.api.RetrofitClient.instance.updateChamaMember(memberId, request)
                android.util.Log.d("ChamaDetailsViewModel", "updateChamaMember response: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}, errorBody=${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    _updateResult.postValue(response.body()?.message ?: "Membership updated!")
                    _myMembership.postValue(response.body()?.member)
                } else {
                    _updateResult.postValue("Failed to update membership.")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChamaDetailsViewModel", "Exception in updateMyMembership: ${e.message}")
                _updateResult.postValue("Network error: ${e.message}")
            }
        }
    }
    private val _myMembership = MutableLiveData<com.example.chamapp.api.ChamaMember?>()
    val myMembership: LiveData<com.example.chamapp.api.ChamaMember?> = _myMembership
    fun fetchMyMembership(chamaId: String) {
        viewModelScope.launch {
            try {
                val response = com.example.chamapp.api.RetrofitClient.instance.getMyMembership(chamaId)
                android.util.Log.d("ChamaDetailsViewModel", "getMyMembership response: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}, errorBody=${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    _myMembership.postValue(response.body()?.membership)
                } else {
                    _error.postValue("Failed to load membership info. Code: ${response.code()} Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }
    private val _chama = MutableLiveData<Chama?>()
    val chama: LiveData<Chama?> = _chama
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    private val _updateResult = MutableLiveData<String?>()
    val updateResult: LiveData<String?> = _updateResult

    fun fetchChamaDetails(chamaId: String) {
        viewModelScope.launch {
            try {
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
                android.util.Log.d("ChamaDetailsViewModel", "Sending updateMemberDetails request: $request")
                val response = RetrofitClient.instance.updateMemberDetails(request)
                android.util.Log.d("ChamaDetailsViewModel", "updateMemberDetails response: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}, errorBody=${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    _updateResult.postValue(response.body()?.message ?: "Update successful!")
                } else {
                    _updateResult.postValue("Failed to update details.")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChamaDetailsViewModel", "Exception in updateMemberDetails: ${e.message}")
                _updateResult.postValue("Network error: ${e.message}")
            }
        }
    }
}
