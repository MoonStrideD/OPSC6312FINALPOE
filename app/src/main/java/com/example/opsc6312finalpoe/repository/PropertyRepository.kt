package com.example.opsc6312finalpoe.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.data.local.AppDatabase
import java.util.UUID

class PropertyRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val propertiesCollection = db.collection("properties")
    private val appDatabase by lazy { AppDatabase.getInstance(context) }

    suspend fun getAllProperties(): List<Property> {
        return try {
            // Try to get from online first
            val onlineProperties = propertiesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Property::class.java)

            // Save to local database for offline use
            val propertyEntities = onlineProperties.map { property ->
                com.example.opsc6312finalpoe.data.local.PropertyEntity.fromProperty(property)
            }
            appDatabase.propertyDao().insertAllProperties(propertyEntities)

            onlineProperties
        } catch (e: Exception) {
            // Fall back to offline data
            Log.d("PropertyRepository", "Using offline data: ${e.message}")
            getPropertiesOffline()
        }
    }

    suspend fun getPropertiesByLandlord(landlordId: String): List<Property> {
        return try {
            // Try online first
            val onlineProperties = propertiesCollection
                .whereEqualTo("landlordId", landlordId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Property::class.java)

            // Save to local database for offline use
            val propertyEntities = onlineProperties.map { property ->
                com.example.opsc6312finalpoe.data.local.PropertyEntity.fromProperty(property)
            }
            appDatabase.propertyDao().insertAllProperties(propertyEntities)

            onlineProperties
        } catch (e: Exception) {
            // Fall back to offline filtering
            Log.d("PropertyRepository", "Using offline data for landlord: ${e.message}")
            getAllProperties().filter { it.landlordId == landlordId }
        }
    }

    suspend fun getPropertiesWithFilters(
        minPrice: Double? = null,
        maxPrice: Double? = null,
        propertyType: String? = null,
        bedrooms: Int? = null,
        location: String? = null
    ): List<Property> {
        return try {
            var query = propertiesCollection.whereEqualTo("status", "available")

            minPrice?.let { query = query.whereGreaterThanOrEqualTo("price", it) }
            maxPrice?.let { query = query.whereLessThanOrEqualTo("price", it) }
            propertyType?.let { query = query.whereEqualTo("propertyType", it) }
            bedrooms?.let { query = query.whereEqualTo("bedrooms", it) }
            location?.let {
                query = query.whereGreaterThanOrEqualTo("location", it)
                    .whereLessThanOrEqualTo("location", it + "\uf8ff")
            }

            val filteredProperties = query.get().await().toObjects(Property::class.java)

            // Save filtered results to local DB
            val propertyEntities = filteredProperties.map { property ->
                com.example.opsc6312finalpoe.data.local.PropertyEntity.fromProperty(property)
            }
            appDatabase.propertyDao().insertAllProperties(propertyEntities)

            filteredProperties
        } catch (e: Exception) {
            Log.d("PropertyRepository", "Using offline filtered data: ${e.message}")
            // For offline, we'll return all properties (filtering would need to be implemented)
            getPropertiesOffline()
        }
    }

    suspend fun addProperty(property: Property): Result<Boolean> {
        return try {
            propertiesCollection.document(property.propertyId).set(property).await()

            // Also save to local database
            val propertyEntity = com.example.opsc6312finalpoe.data.local.PropertyEntity.fromProperty(property)
            appDatabase.propertyDao().insertProperty(propertyEntity)

            Log.d("PropertyRepository", "Property added successfully: ${property.propertyId}")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Failed to add property: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            // Try online first
            propertiesCollection.document(propertyId).get().await().toObject(Property::class.java)
        } catch (e: Exception) {
            // Fall back to offline
            Log.d("PropertyRepository", "Using offline data for property: $propertyId")
            val propertyEntity = appDatabase.propertyDao().getPropertyById(propertyId)
            propertyEntity?.toProperty()
        }
    }

    suspend fun updateProperty(property: Property): Result<Boolean> {
        return try {
            propertiesCollection.document(property.propertyId).set(property).await()

            // Update local database
            val propertyEntity = com.example.opsc6312finalpoe.data.local.PropertyEntity.fromProperty(property)
            appDatabase.propertyDao().updateProperty(propertyEntity)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProperty(propertyId: String): Result<Boolean> {
        return try {
            propertiesCollection.document(propertyId).delete().await()

            // Delete from local database
            appDatabase.propertyDao().deleteProperty(propertyId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getPropertiesOffline(): List<Property> {
        return try {
            val propertyEntities = appDatabase.propertyDao().getAllProperties()
            propertyEntities.map { propertyEntity ->
                propertyEntity.toProperty()
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Failed to get offline properties: ${e.message}")
            emptyList()
        }
    }

    suspend fun clearOfflineData() {
        try {
            appDatabase.propertyDao().clearAllProperties()
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Failed to clear offline data: ${e.message}")
        }
    }

    suspend fun uploadImagesToFirebase(propertyId: String, imageUris: List<Uri>): List<String> {
        val imageUrls = mutableListOf<String>()

        for ((index, imageUri) in imageUris.withIndex()) {
            try {
                val storageRef = storage.reference
                val imageRef = storageRef.child("properties/$propertyId/${UUID.randomUUID()}.jpg")

                val uploadTask = imageRef.putFile(imageUri)
                uploadTask.await()

                val downloadUrl = imageRef.downloadUrl.await()
                imageUrls.add(downloadUrl.toString())

                Log.d("PropertyRepository", "Uploaded image $index: ${downloadUrl}")
            } catch (e: Exception) {
                Log.e("PropertyRepository", "Failed to upload image $index: ${e.message}")
                // Continue with other images even if one fails
            }
        }

        return imageUrls
    }
}