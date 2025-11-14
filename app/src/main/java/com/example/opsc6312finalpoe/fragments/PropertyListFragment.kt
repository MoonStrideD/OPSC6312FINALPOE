package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.adapters.PropertyAdapter
import com.example.opsc6312finalpoe.databinding.FragmentPropertyListBinding
import com.example.opsc6312finalpoe.models.Property
import com.google.android.material.snackbar.Snackbar

class PropertyListFragment : Fragment() {
    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!
    private lateinit var propertyAdapter: PropertyAdapter

    // Sample properties data
    private val sampleProperties = listOf(
        Property(
            propertyId = "1",
            title = "Modern Apartment in Sandton",
            description = "Beautiful modern apartment with stunning city views. Features floor-to-ceiling windows, modern kitchen with stainless steel appliances, and a spacious balcony. Located in a secure building with 24/7 security, gym, and swimming pool.",
            price = 15000.0,
            propertyType = "Apartment",
            bedrooms = 2,
            bathrooms = 2,
            location = "Sandton, Johannesburg",
            amenities = listOf("Pool", "Gym", "Parking", "Security", "Balcony"),
            photos = listOf(""),
            status = "available"
        ),
        Property(
            propertyId = "2",
            title = "Luxury House in Pretoria East",
            description = "Spacious family home with beautiful garden and pool. This 4-bedroom house features an open-plan living area, modern kitchen, study, and entertainment area. Perfect for families with secure perimeter fencing and automated gate.",
            price = 25000.0,
            propertyType = "House",
            bedrooms = 4,
            bathrooms = 3,
            location = "Pretoria East",
            amenities = listOf("Pool", "Garden", "Garage", "Security", "Study"),
            photos = listOf(""),
            status = "available"
        ),
        Property(
            propertyId = "3",
            title = "Cozy Studio in Cape Town CBD",
            description = "Perfect studio apartment for young professionals in the heart of Cape Town. Recently renovated with modern finishes, includes built-in kitchenette, en-suite bathroom, and secure parking. Walking distance to restaurants and shops.",
            price = 8500.0,
            propertyType = "Studio",
            bedrooms = 1,
            bathrooms = 1,
            location = "Cape Town City Centre",
            amenities = listOf("Security", "Parking", "WiFi", "Furnished"),
            photos = listOf(""),
            status = "available"
        ),
        Property(
            propertyId = "4",
            title = "Beachfront Villa in Umhlanga",
            description = "Stunning beachfront property with breathtaking ocean views. This luxury villa features 5 bedrooms, open-plan living areas, gourmet kitchen, private pool, and direct beach access. Perfect for luxury living or holiday rentals.",
            price = 35000.0,
            propertyType = "House",
            bedrooms = 5,
            bathrooms = 4,
            location = "Umhlanga, Durban",
            amenities = listOf("Beach Access", "Pool", "Garden", "Security", "Garage"),
            photos = listOf(""),
            status = "available"
        ),
        Property(
            propertyId = "5",
            title = "Garden Cottage in Randburg",
            description = "Quiet garden cottage perfect for couples or small families. Features open-plan living area, modern kitchen, private garden, and secure parking. Located in a quiet neighborhood with easy access to shopping centers.",
            price = 7500.0,
            propertyType = "Cottage",
            bedrooms = 1,
            bathrooms = 1,
            location = "Randburg, Johannesburg",
            amenities = listOf("Garden", "Parking", "Security", "Pet Friendly"),
            photos = listOf(""),
            status = "available"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupUI()
            setupRecyclerView()
            setupClickListeners()
            loadProperties()
        } catch (e: Exception) {
            Log.e("PropertyListFragment", "Error in onViewCreated", e)
            showErrorState("Failed to load properties: ${e.message}")
        }
    }

    private fun setupUI() {
        binding.toolbar.title = "Available Properties"
        binding.progressBar.visibility = View.GONE

        // Show FAB for adding properties
        binding.fabAddProperty.visibility = View.VISIBLE
        binding.fabAddProperty.setOnClickListener {
            showAddPropertyMessage()
        }
    }

    private fun setupRecyclerView() {
        propertyAdapter = PropertyAdapter(
            properties = sampleProperties, // Pass properties here
            onItemClick = { property ->   // Correct parameter name
                showPropertyDetails(property)
            },
            onFavoriteClick = { property, isFavorite ->
                toggleFavorite(property, isFavorite)
            }
        )

        binding.recyclerViewProperties.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = propertyAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnClearFilters.setOnClickListener {
            loadProperties()
        }

        binding.btnFilter.setOnClickListener {
            showFilterOptions()
        }

        // Setup search functionality
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.etSearch.text.toString())
                true
            } else {
                false
            }
        }

        // Setup chip filters
        binding.chipAll.setOnClickListener {
            loadProperties()
        }

        binding.chipHouses.setOnClickListener {
            filterByType("House")
        }

        binding.chipApartments.setOnClickListener {
            filterByType("Apartment")
        }

        binding.chipStudios.setOnClickListener {
            filterByType("Studio")
        }
    }

    private fun loadProperties() {
        binding.progressBar.visibility = View.VISIBLE

        // Simulate loading delay
        binding.recyclerViewProperties.postDelayed({
            binding.progressBar.visibility = View.GONE
            showPropertiesList(sampleProperties)
            binding.tvResultsCount.text = "Showing ${sampleProperties.size} properties"
        }, 1000)
    }

    private fun showPropertiesList(properties: List<Property>) {
        if (properties.isEmpty()) {
            showEmptyState()
        } else {
            binding.recyclerViewProperties.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
            propertyAdapter.updateProperties(properties)
        }
    }

    private fun showEmptyState() {
        binding.recyclerViewProperties.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.tvResultsCount.text = "No properties found"
    }

    private fun showErrorState(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewProperties.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.tvResultsCount.text = message
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            loadProperties()
            return
        }

        val filteredProperties = sampleProperties.filter { property ->
            property.title.contains(query, ignoreCase = true) ||
                    property.location.contains(query, ignoreCase = true) ||
                    property.description.contains(query, ignoreCase = true) ||
                    property.propertyType.contains(query, ignoreCase = true)
        }

        if (filteredProperties.isEmpty()) {
            showEmptyState()
            binding.tvResultsCount.text = "No results for '$query'"
        } else {
            showPropertiesList(filteredProperties)
            binding.tvResultsCount.text = "Found ${filteredProperties.size} properties for '$query'"
        }
    }

    private fun filterByType(type: String) {
        val filteredProperties = sampleProperties.filter { it.propertyType.equals(type, ignoreCase = true) }

        if (filteredProperties.isEmpty()) {
            showEmptyState()
            binding.tvResultsCount.text = "No $type properties found"
        } else {
            showPropertiesList(filteredProperties)
            binding.tvResultsCount.text = "$type Properties (${filteredProperties.size})"
        }
    }

    private fun showFilterOptions() {
        val filterOptions = arrayOf("Price: Low to High", "Price: High to Low", "Bedrooms: Most First", "Bedrooms: Least First")

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sort Properties")
            .setItems(filterOptions) { _, which ->
                when (which) {
                    0 -> sortByPrice(true)
                    1 -> sortByPrice(false)
                    2 -> sortByBedrooms(true)
                    3 -> sortByBedrooms(false)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sortByPrice(ascending: Boolean) {
        val sortedProperties = if (ascending) {
            sampleProperties.sortedBy { it.price }
        } else {
            sampleProperties.sortedByDescending { it.price }
        }
        showPropertiesList(sortedProperties)
        binding.tvResultsCount.text = "Sorted by price ${if (ascending) "low to high" else "high to low"}"
    }

    private fun sortByBedrooms(descending: Boolean) {
        val sortedProperties = if (descending) {
            sampleProperties.sortedByDescending { it.bedrooms }
        } else {
            sampleProperties.sortedBy { it.bedrooms }
        }
        showPropertiesList(sortedProperties)
        binding.tvResultsCount.text = "Sorted by bedrooms ${if (descending) "most first" else "least first"}"
    }

    private fun showPropertyDetails(property: Property) {
        // Navigate to property details fragment
        val propertyDetailsFragment = PropertyDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("property", property)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, propertyDetailsFragment)
            .addToBackStack("property_details")
            .commit()
    }

    private fun toggleFavorite(property: Property, isFavorite: Boolean) {
        val action = if (isFavorite) "added to" else "removed from"
        Snackbar.make(binding.root,
            "${property.title} $action favorites",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showAddPropertyMessage() {
        Snackbar.make(binding.root,
            "Add Property feature would open here",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}