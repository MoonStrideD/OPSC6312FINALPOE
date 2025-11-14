package com.example.opsc6312finalpoe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Property(
    var propertyId: String = "",
    var landlordId: String = "",
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var propertyType: String = "",
    var bedrooms: Int = 0,
    var bathrooms: Int = 0,
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var amenities: List<String> = emptyList(),
    var photos: List<String> = emptyList(),
    var status: String = "available",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    fun getFormattedPrice(): String {
        return "R${"%.0f".format(price)}"
    }

    fun getBedroomText(): String {
        return if (bedrooms == 1) "1 bedroom" else "$bedrooms bedrooms"
    }

    fun getBathroomText(): String {
        return if (bathrooms == 1) "1 bathroom" else "$bathrooms bathrooms"
    }

    fun getMainPhoto(): String {
        return photos.firstOrNull() ?: ""
    }
}