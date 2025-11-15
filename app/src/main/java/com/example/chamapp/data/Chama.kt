package com.example.chamapp.data

data class Chama(
    val chamaId: String,
    val chamaName: String?,
    val totalBalance: Double?,
    val members: List<ChamaMemberRelation>?
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
