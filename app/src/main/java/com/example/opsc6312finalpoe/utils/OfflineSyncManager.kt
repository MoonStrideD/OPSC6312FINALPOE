package com.example.opsc6312finalpoe.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.opsc6312finalpoe.data.local.AppDatabase
import com.example.opsc6312finalpoe.data.local.PropertyEntity
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.repository.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OfflineSyncManager(private val context: Context) {
    private val propertyRepository = PropertyRepository()
    private val appDatabase = AppDatabase.getInstance(context)
    private val sharedPreferencesHelper = SharedPreferencesHelper(context)

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    suspend fun syncProperties() {
        if (!isOnline()) {
            Log.d("OfflineSync", "No internet connection, skipping sync")
            return
        }

        try {
            // Get properties from online source
            val onlineProperties = propertyRepository.getAllProperties()

            // Save to local database
            val propertyEntities = onlineProperties.map { PropertyEntity.fromProperty(it) }
            appDatabase.propertyDao().insertAllProperties(propertyEntities)

            Log.d("OfflineSync", "Synced ${onlineProperties.size} properties to local database")
        } catch (e: Exception) {
            Log.e("OfflineSync", "Sync failed: ${e.message}")
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

    suspend fun searchPropertiesOffline(query: String): List<Property> {
        return try {
            val propertyEntities = appDatabase.propertyDao().searchProperties(query)
            propertyEntities.map { it.toProperty() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun schedulePeriodicSync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncProperties()
            } catch (e: Exception) {
                Log.e("OfflineSync", "Periodic sync failed: ${e.message}")
            }
        }
    }
}