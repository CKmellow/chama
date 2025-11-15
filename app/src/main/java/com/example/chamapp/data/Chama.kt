package com.example.chamapp.data

// Updated Chama data class to match UI/adapter requirements

data class Chama(
    val id: String? = null,
    val name: String? = null,
    val role: String? = null,
    val myContributions: String? = null,
    val totalBalance: String? = null,
    val status: String? = null,
    val statusColor: String? = null,
    val nextMeeting: String? = null,
    val members: List<ChamaMemberRelation>? = null
)

data class ChamaMemberRelation(
    val userId: String?,
    val firstName: String?,
    val lastName: String?,
    val role: String?,
    val contributionAmount: Double?,
    val status: String?,
    val email: String?,
    val phoneNumber: String?
)
