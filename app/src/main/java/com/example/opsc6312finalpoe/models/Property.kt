package com.example.opsc6312finalpoe.models

data class Property(
    val propertyId: String = "",
    val landlordId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val propertyType: String = "", // house, apartment, room, studio
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val amenities: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val status: String = "available", // available, rented, pending
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)