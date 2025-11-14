package com.example.opsc6312finalpoe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.opsc6312finalpoe.models.Property

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey
    val propertyId: String,
    val landlordId: String,
    val title: String,
    val description: String,
    val price: Double,
    val propertyType: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val amenities: String, // Store as JSON string
    val photos: String, // Store as JSON string
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromProperty(property: Property): PropertyEntity {
            return PropertyEntity(
                propertyId = property.propertyId,
                landlordId = property.landlordId,
                title = property.title,
                description = property.description,
                price = property.price,
                propertyType = property.propertyType,
                bedrooms = property.bedrooms,
                bathrooms = property.bathrooms,
                location = property.location,
                latitude = property.latitude,
                longitude = property.longitude,
                amenities = property.amenities.joinToString(","),
                photos = property.photos.joinToString(","),
                status = property.status,
                createdAt = property.createdAt,
                updatedAt = property.updatedAt
            )
        }
    }

    fun toProperty(): Property {
        return Property(
            propertyId = propertyId,
            landlordId = landlordId,
            title = title,
            description = description,
            price = price,
            propertyType = propertyType,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            location = location,
            latitude = latitude,
            longitude = longitude,
            amenities = if (amenities.isNotEmpty()) amenities.split(",") else emptyList(),
            photos = if (photos.isNotEmpty()) photos.split(",") else emptyList(),
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}