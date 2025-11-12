package com.example.opsc6312finalpoe.models

data class User(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String = "",
    val role: String = "tenant", // tenant, landlord, admin
    val preferredLanguage: String = "en", // en, af, zu
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)