package com.example.chamapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChamaMember(
    val id: String,
    val chamaId: String,
    val userId: String,
    val role: String?,
    val contributionAmount: Double?,
    val joinedAt: String?,
    val status: String?,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
): android.os.Parcelable
