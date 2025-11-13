package com.example.opsc6312finalpoe.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyViewModel(private val propertyRepository: PropertyRepository) : ViewModel() {
    private val _properties = MutableStateFlow<List<com.example.opsc6312finalpoe.models.Property>>(emptyList())
    val properties: StateFlow<List<com.example.opsc6312finalpoe.models.Property>> = _properties.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    fun loadProperties() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val propertiesList = propertyRepository.getAllProperties()
                _properties.value = propertiesList
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Failed to load properties")
            }
        }
    }

    fun searchProperties(
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
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Failed to add property")
            }
        }
    }

    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}