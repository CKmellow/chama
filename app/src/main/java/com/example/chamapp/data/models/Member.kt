package com.example.chamapp.data.models

data class Member(
    val name: String,
    val role: String,
    val profileImageUrl: String? = null // Allow for members without a profile picture
)

