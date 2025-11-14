package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.databinding.FragmentPropertyDetailsBinding
import com.example.opsc6312finalpoe.models.Property

class PropertyDetailsFragment : Fragment() {
    private var _binding: FragmentPropertyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var property: Property

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get property from arguments
        property = arguments?.getParcelable("property") ?: return

        setupUI()
        displayPropertyDetails()
    }

    private fun setupUI() {
        binding.toolbar.title = "Property Details"
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set up contact button
        binding.btnContact.setOnClickListener {
            // Handle contact action
            showContactDialog()
        }

        // Set up favorite button
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun displayPropertyDetails() {
        with(property) {
            binding.tvTitle.text = title
            binding.tvPrice.text = getFormattedPrice() + "/month"
            binding.tvLocation.text = location
            binding.tvPropertyType.text = propertyType
            binding.tvBedrooms.text = getBedroomText()
            binding.tvBathrooms.text = getBathroomText()
            binding.tvDescription.text = description

            // Display amenities
            val amenitiesText = if (amenities.isNotEmpty()) {
                amenities.joinToString(" • ")
            } else {
                "No amenities listed"
            }
            binding.tvAmenities.text = amenitiesText

            // Set status
            if (status == "available") {
                binding.tvStatus.text = "Available"
                binding.tvStatus.setBackgroundResource(R.drawable.status_available)
            } else {
                binding.tvStatus.text = "Rented"
                binding.tvStatus.setBackgroundResource(R.drawable.status_rented)
            }
        }
    }

    private fun showContactDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Contact Landlord")
            .setMessage("Would you like to contact the landlord about ${property.title}?")
            .setPositiveButton("Call") { _, _ ->
                // Handle call action
                showMessage("Call feature would open here")
            }
            .setNegativeButton("Message") { _, _ ->
                // Handle message action
                showMessage("Messaging feature would open here")
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun toggleFavorite() {
        val isFavorite = !binding.btnFavorite.isSelected
        binding.btnFavorite.isSelected = isFavorite

        val message = if (isFavorite) {
            "Added to favorites"
        } else {
            "Removed from favorites"
        }
        showMessage("${property.title} $message")
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}