package com.example.chamapp.data

import com.google.gson.annotations.SerializedName


// Data class for a Chama, including members
data class Chama(
    @SerializedName("chama_id") val id: String,
    @SerializedName("chama_name") val chamaName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("chama_type") val chamaType: String?,
    @SerializedName("members") val members: List<ChamaMember> = emptyList(),
    // Add other fields as needed
)
