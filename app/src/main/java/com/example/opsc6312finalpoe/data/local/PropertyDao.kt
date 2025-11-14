package com.example.opsc6312finalpoe.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    suspend fun getAllProperties(): List<PropertyEntity>

    @Query("SELECT * FROM properties WHERE propertyId = :propertyId")
    suspend fun getPropertyById(propertyId: String): PropertyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProperties(properties: List<PropertyEntity>)

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Query("DELETE FROM properties WHERE propertyId = :propertyId")
    suspend fun deleteProperty(propertyId: String)

    @Query("DELETE FROM properties")
    suspend fun clearAllProperties()

    // Flow versions for real-time updates (optional)
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun getAllPropertiesFlow(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE propertyId = :propertyId")
    fun getPropertyByIdFlow(propertyId: String): Flow<PropertyEntity?>
}