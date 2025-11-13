package com.example.opsc6312finalpoe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE propertyId = :propertyId")
    suspend fun getPropertyById(propertyId: String): PropertyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProperties(properties: List<PropertyEntity>)

    @Query("DELETE FROM properties")
    suspend fun clearAllProperties()

    @Query("SELECT * FROM properties WHERE location LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchProperties(query: String): Flow<List<PropertyEntity>>

    // Add this method to get properties without Flow for direct access
    @Query("SELECT * FROM properties")
    suspend fun getAllPropertiesDirect(): List<PropertyEntity>
}