package com.example.opsc6312finalpoe.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.databinding.FragmentAddPropertyBinding
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.repository.AuthRepository
import com.example.opsc6312finalpoe.repository.PropertyRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        // Setup property type spinner
        val propertyTypes = arrayOf("House", "Apartment", "Room", "Studio")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPropertyType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectImages.setOnClickListener {
            selectImages()
        }

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
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else if (data?.data != null) {
                selectedImages.add(data.data!!)
            }
            updateSelectedImagesCount()
        }
    }

    private fun updateSelectedImagesCount() {
        binding.tvSelectedImages.text = "Selected images: ${selectedImages.size}"
    }

    private fun validateInputs(): Boolean {
        // Add validation logic here
        return true
    }

    private fun addProperty() {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val landlordId = authRepository.getCurrentUser()?.uid ?: ""
                val propertyId = UUID.randomUUID().toString()

                // Upload images first
                val imageUrls = uploadImages(propertyId)

                val property = Property(
                    propertyId = propertyId,
                    landlordId = landlordId,
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    price = binding.etPrice.text.toString().toDouble(),
                    propertyType = binding.spinnerPropertyType.selectedItem.toString(),
                    bedrooms = binding.etBedrooms.text.toString().toInt(),
                    bathrooms = binding.etBathrooms.text.toString().toInt(),
                    location = binding.etLocation.text.toString(),
                    latitude = 0.0, // You can add map integration later
                    longitude = 0.0,
                    amenities = emptyList(),
                    photos = imageUrls,
                    status = "available"
                )

                val result = propertyRepository.addProperty(property)
                if (result.isSuccess) {
                    // Show success message and clear form
                    binding.progressBar.visibility = View.GONE
                    clearForm()
                } else {
                    // Show error message
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                // Handle error
            }
        }
    }

    private suspend fun uploadImages(propertyId: String): List<String> {
        val storage = FirebaseStorage.getInstance()
        val imageUrls = mutableListOf<String>()

        for (imageUri in selectedImages) {
            val fileName = "properties/$propertyId/${UUID.randomUUID()}"
            val storageRef = storage.reference.child(fileName)

            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            imageUrls.add(downloadUrl.toString())
        }

        return imageUrls
    }

    private fun clearForm() {
        binding.etTitle.text.clear()
        binding.etDescription.text.clear()
        binding.etPrice.text.clear()
        binding.etBedrooms.text.clear()
        binding.etBathrooms.text.clear()
        binding.etLocation.text.clear()
        selectedImages.clear()
        updateSelectedImagesCount()
    }

    companion object {
        private const val PICK_IMAGES_REQUEST = 1001
    }
}