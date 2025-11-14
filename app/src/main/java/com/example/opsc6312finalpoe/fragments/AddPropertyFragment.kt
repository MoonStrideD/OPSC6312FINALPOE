package com.example.opsc6312finalpoe.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.opsc6312finalpoe.databinding.FragmentAddPropertyBinding
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.repository.AuthRepository
import com.example.opsc6312finalpoe.repository.PropertyRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AddPropertyFragment : Fragment() {
    private lateinit var binding: FragmentAddPropertyBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var propertyRepository: PropertyRepository
    private val selectedImages = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
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
            if (validateInputs()) {
                addProperty()
            }
        }
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImages.clear()
            data?.clipData?.let { clipData ->
                val count = clipData.itemCount
                for (i in 0 until count) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } ?: data?.data?.let { singleUri ->
                selectedImages.add(singleUri)
            }
            updateSelectedImagesCount()
        }
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

        if (selectedImages.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_SHORT).show()
            return false
        }

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
                    return@launch
                }

                val propertyId = UUID.randomUUID().toString()
                val imageUrls = uploadImages(propertyId)

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
                    latitude = 0.0, // You might want to implement geocoding here
                    longitude = 0.0, // You might want to implement geocoding here
                    amenities = emptyList(),
                    photos = imageUrls,
                    status = "available",
                    createdAt = System.currentTimeMillis()
                )

                val result = propertyRepository.addProperty(property)
                binding.progressBar.visibility = View.GONE
                binding.btnAddProperty.isEnabled = true

                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Property added successfully!", Toast.LENGTH_SHORT).show()
                    clearForm()
                    // Optionally navigate back to previous fragment
                    // findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Failed to add property: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnAddProperty.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun uploadImages(propertyId: String): List<String> {
        val storage = FirebaseStorage.getInstance()
        val imageUrls = mutableListOf<String>()

        for ((index, imageUri) in selectedImages.withIndex()) {
            try {
                val fileName = "properties/$propertyId/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)

                // Upload the image
                storageRef.putFile(imageUri).await()

                // Get the download URL
                val downloadUrl = storageRef.downloadUrl.await()
                imageUrls.add(downloadUrl.toString())

                // Update progress if needed
                binding.tvSelectedImages.text = "Uploading images... (${index + 1}/${selectedImages.size})"
            } catch (e: Exception) {
                // Log the error but continue with other images
                println("Failed to upload image $index: ${e.message}")
            }
        }

        return imageUrls
    }

    private fun clearForm() {
        binding.etTitle.text?.clear()
        binding.etDescription.text?.clear()
        binding.etPrice.text?.clear()
        binding.etBedrooms.text?.clear()
        binding.etBathrooms.text?.clear()
        binding.etLocation.text?.clear()
        selectedImages.clear()
        updateSelectedImagesCount()
    }

    companion object {
        private const val PICK_IMAGES_REQUEST = 1001
    }
}