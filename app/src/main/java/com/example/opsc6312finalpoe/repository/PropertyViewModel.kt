package com.example.opsc6312finalpoe.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyViewModel(private val propertyRepository: PropertyRepository) : ViewModel() {
    private val _properties = MutableStateFlow<List<com.example.opsc6312finalpoe.models.Property>>(emptyList())
    val properties: StateFlow<List<com.example.opsc6312finalpoe.models.Property>> = _properties.asStateFlow()

    private val _userProperties = MutableStateFlow<List<com.example.opsc6312finalpoe.models.Property>>(emptyList())
    val userProperties: StateFlow<List<com.example.opsc6312finalpoe.models.Property>> = _userProperties.asStateFlow()

    private val _filteredProperties = MutableStateFlow<List<com.example.opsc6312finalpoe.models.Property>>(emptyList())
    val filteredProperties: StateFlow<List<com.example.opsc6312finalpoe.models.Property>> = _filteredProperties.asStateFlow()

    private val _favoriteProperties = MutableStateFlow<Set<String>>(emptySet())
    val favoriteProperties: StateFlow<Set<String>> = _favoriteProperties.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    // Fixed isLoading - create a separate StateFlow for it
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProperties()
        setupLoadingObserver()
    }

    private fun setupLoadingObserver() {
        viewModelScope.launch {
            _loadingState.collect { loadingState ->
                _isLoading.value = (loadingState == LoadingState.Loading)
            }
        }
    }

    fun loadProperties() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val propertiesList = propertyRepository.getAllProperties()
                _properties.value = propertiesList
                _filteredProperties.value = propertiesList
                _loadingState.value = LoadingState.Success
                Log.d("PropertyViewModel", "Successfully loaded ${propertiesList.size} properties")
            } catch (e: Exception) {
                val errorMessage = "Failed to load properties: ${e.message}"
                _loadingState.value = LoadingState.Error(errorMessage)
                Log.e("PropertyViewModel", errorMessage, e)
            }
        }
    }

    fun loadUserProperties(landlordId: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val userPropertiesList = propertyRepository.getPropertiesByLandlord(landlordId)
                _userProperties.value = userPropertiesList
                _loadingState.value = LoadingState.Success
                Log.d("PropertyViewModel", "Loaded ${userPropertiesList.size} properties for landlord: $landlordId")

                // Debug: log each property
                userPropertiesList.forEachIndexed { index, property ->
                    Log.d("PropertyViewModel", "User Property $index: ${property.title} (ID: ${property.propertyId})")
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to load your properties: ${e.message}"
                _loadingState.value = LoadingState.Error(errorMessage)
                Log.e("PropertyViewModel", errorMessage, e)
            }
        }
    }

    // Test method to add a sample property
    fun testPropertyAddition() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val testProperty = com.example.opsc6312finalpoe.models.Property(
                    propertyId = "test-${System.currentTimeMillis()}",
                    landlordId = "test-landlord-${System.currentTimeMillis()}",
                    title = "Test Property ${System.currentTimeMillis()}",
                    description = "This is a test property added for debugging",
                    price = 1500.0,
                    propertyType = "Apartment",
                    bedrooms = 2,
                    bathrooms = 1,
                    location = "Test Location",
                    latitude = -26.2041,
                    longitude = 28.0473,
                    amenities = listOf("WiFi", "Parking"),
                    photos = emptyList(),
                    status = "available",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val result = propertyRepository.addProperty(testProperty)
                if (result.isSuccess) {
                    _loadingState.value = LoadingState.Success
                    Log.d("PropertyViewModel", "✅ Test property added successfully: ${testProperty.title}")
                } else {
                    _loadingState.value = LoadingState.Error("Failed to add test property")
                    Log.e("PropertyViewModel", "❌ Failed to add test property")
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error("Test property failed: ${e.message}")
                Log.e("PropertyViewModel", "❌ Failed to add test property", e)
            }
        }
    }

    // Test method to add a property for current user
    fun testPropertyForCurrentUser(userId: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val testProperty = com.example.opsc6312finalpoe.models.Property(
                    propertyId = "user-test-${System.currentTimeMillis()}",
                    landlordId = userId,
                    title = "My Test Property ${System.currentTimeMillis()}",
                    description = "This is my test property",
                    price = 2000.0,
                    propertyType = "House",
                    bedrooms = 3,
                    bathrooms = 2,
                    location = "My Location",
                    latitude = -26.2041,
                    longitude = 28.0473,
                    amenities = listOf("Garden", "Garage"),
                    photos = emptyList(),
                    status = "available",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val result = propertyRepository.addProperty(testProperty)
                if (result.isSuccess) {
                    _loadingState.value = LoadingState.Success
                    Log.d("PropertyViewModel", "✅ User test property added successfully: ${testProperty.title}")
                    Log.d("PropertyViewModel", "✅ Property landlord ID: ${testProperty.landlordId}")
                    Log.d("PropertyViewModel", "✅ Current user ID: $userId")
                } else {
                    _loadingState.value = LoadingState.Error("Failed to add user test property")
                    Log.e("PropertyViewModel", "❌ Failed to add user test property")
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error("User test property failed: ${e.message}")
                Log.e("PropertyViewModel", "❌ Failed to add user test property", e)
            }
        }
    }

    // Simple search by query string
    fun searchProperties(query: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val results = if (query.isBlank()) {
                    _properties.value
                } else {
                    _properties.value.filter { property ->
                        property.title.contains(query, ignoreCase = true) ||
                                property.location.contains(query, ignoreCase = true) ||
                                property.description.contains(query, ignoreCase = true) ||
                                property.propertyType.contains(query, ignoreCase = true)
                    }
                }
                _filteredProperties.value = results
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Search failed")
            }
        }
    }

    // Filter by property types
    fun filterPropertiesByType(types: List<String>) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val results = if (types.isEmpty()) {
                    _properties.value
                } else {
                    _properties.value.filter { property ->
                        types.any { type ->
                            property.propertyType.equals(type, ignoreCase = true)
                        }
                    }
                }
                _filteredProperties.value = results
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Filter failed")
            }
        }
    }

    // Favorite methods
    fun addToFavorites(propertyId: String) {
        viewModelScope.launch {
            val currentFavorites = _favoriteProperties.value.toMutableSet()
            currentFavorites.add(propertyId)
            _favoriteProperties.value = currentFavorites
        }
    }

    fun removeFromFavorites(propertyId: String) {
        viewModelScope.launch {
            val currentFavorites = _favoriteProperties.value.toMutableSet()
            currentFavorites.remove(propertyId)
            _favoriteProperties.value = currentFavorites
        }
    }

    // Original search with filters (keep this for other uses)
    fun searchPropertiesWithFilters(
        minPrice: Double? = null,
        maxPrice: Double? = null,
        propertyType: String? = null,
        bedrooms: Int? = null,
        location: String? = null
    ) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val filteredProperties = propertyRepository.getPropertiesWithFilters(
                    minPrice, maxPrice, propertyType, bedrooms, location
                )
                _properties.value = filteredProperties
                _filteredProperties.value = filteredProperties
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun addProperty(property: com.example.opsc6312finalpoe.models.Property) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                propertyRepository.addProperty(property)
                // Reload properties to include the new one
                loadProperties()
                Log.d("PropertyViewModel", "✅ Property added: ${property.title}")
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Failed to add property")
                Log.e("PropertyViewModel", "❌ Failed to add property: ${property.title}", e)
            }
        }
    }

    fun refreshProperties() {
        loadProperties()
    }

    fun refreshUserProperties(landlordId: String) {
        loadUserProperties(landlordId)
    }

    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}