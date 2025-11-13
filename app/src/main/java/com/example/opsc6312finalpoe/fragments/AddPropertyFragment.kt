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
import androidx.lifecycle.lifecycleScope
import com.example.opsc6312finalpoe.databinding.FragmentAddPropertyBinding
import com.example.opsc6312finalpoe.models.Property
import com.example.opsc6312finalpoe.repository.AuthRepository
import com.example.opsc6312finalpoe.repository.PropertyRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
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
        val propertyTypes = arrayOf("House", "Apartment", "Room", "Studio")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPropertyType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectImages.setOnClickListener { selectImages() }
        binding.btnAddProperty.setOnClickListener {
            if (validateInputs()) addProperty()
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
            data?.clipData?.let {
                val count = it.itemCount
                for (i in 0 until count) {
                    val imageUri = it.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } ?: data?.data?.let { selectedImages.add(it) }
            updateSelectedImagesCount()
        }
    }

    private fun updateSelectedImagesCount() {
        binding.tvSelectedImages.text = "Selected images: ${selectedImages.size}"
    }

    private fun validateInputs(): Boolean {
        return true
    }

    private fun addProperty() {
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val landlordId = authRepository.getCurrentUser()?.uid ?: ""
                val propertyId = UUID.randomUUID().toString()

                val imageUrls = uploadImages(propertyId)

                val price = binding.etPrice.text?.toString().orEmpty().toDoubleOrNull() ?: 0.0
                val bedrooms = binding.etBedrooms.text?.toString().orEmpty().toIntOrNull() ?: 0
                val bathrooms = binding.etBathrooms.text?.toString().orEmpty().toIntOrNull() ?: 0

                val property = Property(
                    propertyId = propertyId,
                    landlordId = landlordId,
                    title = binding.etTitle.text?.toString().orEmpty(),
                    description = binding.etDescription.text?.toString().orEmpty(),
                    price = price,
                    propertyType = binding.spinnerPropertyType.selectedItem.toString(),
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    location = binding.etLocation.text?.toString().orEmpty(),
                    latitude = 0.0,
                    longitude = 0.0,
                    amenities = emptyList(),
                    photos = imageUrls,
                    status = "available"
                )

                val result = propertyRepository.addProperty(property)
                binding.progressBar.visibility = View.GONE
                if (result.isSuccess) clearForm()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun uploadImages(propertyId: String): List<String> {
        val storage = FirebaseStorage.getInstance()
        val imageUrls = mutableListOf<String>()

        for (imageUri in selectedImages) {
            val fileName = "properties/$propertyId/${UUID.randomUUID()}"
            val storageRef = storage.reference.child(fileName)

            // suspend until upload completes and then get download URL
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            imageUrls.add(downloadUrl.toString())
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
