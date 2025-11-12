package com.example.opsc6312finalpoe.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.data.local.AppDatabase
import com.example.opsc6312finalpoe.data.local.PropertyEntity

class PropertyRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val propertiesCollection = db.collection("properties")
    private val appDatabase = AppDatabase.getInstance(context)

    suspend fun getAllProperties(): List<Property> {
        return try {
            // Try to get from online first
            val onlineProperties = propertiesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Property::class.java)

            // Save to local database for offline use
            val propertyEntities = onlineProperties.map { PropertyEntity.fromProperty(it) }
            appDatabase.propertyDao().insertAllProperties(propertyEntities)

            onlineProperties
        } catch (e: Exception) {
            // Fall back to offline data
            Log.d("PropertyRepository", "Using offline data: ${e.message}")
            getPropertiesOffline()
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
            val propertyEntities = filteredProperties.map { PropertyEntity.fromProperty(it) }
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
            val propertyEntity = PropertyEntity.fromProperty(property)
            appDatabase.propertyDao().insertProperty(propertyEntity)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            // Try online first
            propertiesCollection.document(propertyId).get().await().toObject(Property::class.java)
        } catch (e: Exception) {
            // Fall back to offline
            val propertyEntity = appDatabase.propertyDao().getPropertyById(propertyId)
            propertyEntity?.toProperty()
        }
    }

    suspend fun getPropertiesByLandlord(landlordId: String): List<Property> {
        return try {
            propertiesCollection
                .whereEqualTo("landlordId", landlordId)
                .get()
                .await()
                .toObjects(Property::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPropertiesOffline(): List<Property> {
        return try {
            val propertyEntities = appDatabase.propertyDao().getAllProperties()
            propertyEntities.map { it.toProperty() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun clearOfflineData() {
        appDatabase.propertyDao().clearAllProperties()
    }
}