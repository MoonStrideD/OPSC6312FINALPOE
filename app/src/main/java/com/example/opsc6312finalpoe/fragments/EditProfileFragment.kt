package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.databinding.FragmentEditProfileBinding
import com.example.opsc6312finalpoe.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository(requireActivity())
        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = authRepository.getCurrentUserData()
            user?.let {
                binding.etFirstName.setText(it.firstName)
                binding.etLastName.setText(it.lastName)
                binding.etEmail.setText(it.email)
                binding.etPhone.setText(it.phoneNumber)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            if (validateInputs()) {
                updateProfile()
            }
        }

        // Add back button functionality if needed
        // binding.btnBack.setOnClickListener {
        //     requireActivity().onBackPressed()
        // }
    }

    private fun validateInputs(): Boolean {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val phone = binding.etPhone.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            // Show error message
            return false
        }

        return true
    }

    private fun updateProfile() {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userId = authRepository.getCurrentUser()?.uid
                val firstName = binding.etFirstName.text.toString()
                val lastName = binding.etLastName.text.toString()
                val phone = binding.etPhone.text.toString()

                if (userId != null) {
                    // Update user data in Firestore
                    val result = authRepository.updateUserProfile(userId, firstName, lastName, phone)

                    if (result.isSuccess) {
                        // Show success message and go back
                        requireActivity().onBackPressed()
                    } else {
                        // Show error message
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}