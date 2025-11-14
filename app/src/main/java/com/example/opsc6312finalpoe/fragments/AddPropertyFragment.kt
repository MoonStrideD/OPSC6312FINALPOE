package com.example.opsc6312finalpoe.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.opsc6312finalpoe.databinding.FragmentAddPropertyBinding
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.repository.AuthRepository
import com.example.opsc6312finalpoe.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class AddPropertyFragment : Fragment() {
    private var _binding: FragmentAddPropertyBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository
    private lateinit var propertyRepository: PropertyRepository
    private val selectedImages = mutableListOf<Uri>()

    // Updated activity result launcher (non-deprecated)
    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImages.clear()
            result.data?.let { data ->
                data.clipData?.let { clipData ->
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        val imageUri = clipData.getItemAt(i).uri
                        selectedImages.add(imageUri)
                    }
                } ?: data.data?.let { singleUri ->
                    selectedImages.add(singleUri)
                }
            }
            updateSelectedImagesCount()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository(requireActivity())
        propertyRepository = PropertyRepository(requireContext())

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val propertyTypes = arrayOf("House", "Apartment", "Room", "Studio", "Condo", "Townhouse")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPropertyType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectImages.setOnClickListener { selectImages() }
        binding.btnAddProperty.setOnClickListener {
            if (validateInputs()) addProperty()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set navigation icon click listener for toolbar
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImagesLauncher.launch(intent)
    }

    private fun updateSelectedImagesCount() {
        binding.tvSelectedImages.text = "Selected images: ${selectedImages.size}"
    }

    private fun validateInputs(): Boolean {
        val title = binding.etTitle.text?.toString().orEmpty().trim()
        val description = binding.etDescription.text?.toString().orEmpty().trim()
        val priceText = binding.etPrice.text?.toString().orEmpty().trim()
        val bedroomsText = binding.etBedrooms.text?.toString().orEmpty().trim()
        val bathroomsText = binding.etBathrooms.text?.toString().orEmpty().trim()
        val location = binding.etLocation.text?.toString().orEmpty().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return false
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return false
        }

        if (priceText.isEmpty()) {
            binding.etPrice.error = "Price is required"
            return false
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            binding.etPrice.error = "Please enter a valid price"
            return false
        }

        if (bedroomsText.isEmpty()) {
            binding.etBedrooms.error = "Number of bedrooms is required"
            return false
        }

        val bedrooms = bedroomsText.toIntOrNull()
        if (bedrooms == null || bedrooms < 0) {
            binding.etBedrooms.error = "Please enter a valid number of bedrooms"
            return false
        }

        if (bathroomsText.isEmpty()) {
            binding.etBathrooms.error = "Number of bathrooms is required"
            return false
        }

        val bathrooms = bathroomsText.toIntOrNull()
        if (bathrooms == null || bathrooms < 0) {
            binding.etBathrooms.error = "Please enter a valid number of bathrooms"
            return false
        }

        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return false
        }

        // REMOVED the image validation - properties can be added without images
        return true
    }

    private fun addProperty() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnAddProperty.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val landlordId = authRepository.getCurrentUser()?.uid
                if (landlordId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnAddProperty.isEnabled = true
                    return@launch
                }

                val propertyId = UUID.randomUUID().toString()
                val imageUrls = mutableListOf<String>()

                // Upload images only if they exist
                if (selectedImages.isNotEmpty()) {
                    binding.tvSelectedImages.text = "Uploading images..."
                    val uploadedUrls = propertyRepository.uploadImagesToFirebase(propertyId, selectedImages)
                    imageUrls.addAll(uploadedUrls)
                } else {
                    // Use a default placeholder image or empty list
                    imageUrls.add("") // Or use a placeholder URL: "https://example.com/placeholder.jpg"
                }

                val price = binding.etPrice.text?.toString().orEmpty().toDouble()
                val bedrooms = binding.etBedrooms.text?.toString().orEmpty().toInt()
                val bathrooms = binding.etBathrooms.text?.toString().orEmpty().toInt()

                val property = Property(
                    propertyId = propertyId,
                    landlordId = landlordId,
                    title = binding.etTitle.text?.toString().orEmpty().trim(),
                    description = binding.etDescription.text?.toString().orEmpty().trim(),
                    price = price,
                    propertyType = binding.spinnerPropertyType.selectedItem.toString(),
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    location = binding.etLocation.text?.toString().orEmpty().trim(),
                    latitude = 0.0,
                    longitude = 0.0,
                    amenities = emptyList(),
                    photos = imageUrls,
                    status = "available",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val result = propertyRepository.addProperty(property)
                binding.progressBar.visibility = View.GONE
                binding.btnAddProperty.isEnabled = true

                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Property added successfully!", Toast.LENGTH_SHORT).show()
                    clearForm()
                    // Navigate back
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    Toast.makeText(requireContext(), "Failed to add property: $errorMessage", Toast.LENGTH_LONG).show()
                    Log.e("AddPropertyFragment", "Add property failed: $errorMessage")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnAddProperty.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("AddPropertyFragment", "Error adding property", e)
            }
        }
    }

    private fun clearForm() {
        binding.etTitle.text?.clear()
        binding.etDescription.text?.clear()
        binding.etPrice.text?.clear()
        binding.etBedrooms.text?.clear()
        binding.etBathrooms.text?.clear()
        binding.etLocation.text?.clear()
        binding.spinnerPropertyType.setSelection(0)
        selectedImages.clear()
        updateSelectedImagesCount()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}