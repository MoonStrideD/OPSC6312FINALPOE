package com.example.opsc6312finalpoe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.activities.SettingsActivity
import com.example.opsc6312finalpoe.databinding.FragmentProfileBinding
import com.example.opsc6312finalpoe.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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
                binding.tvUserName.text = getString(R.string.full_name, it.firstName, it.lastName)
                binding.tvUserEmail.text = it.email
                binding.tvUserPhone.text = it.phoneNumber
                binding.tvUserRole.text = "Role: ${it.role.capitalize()}"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            authRepository.logout()
            requireActivity().finish()
        }

        binding.btnEditProfile.setOnClickListener {
            // Navigate to edit profile
            (activity as? MainActivity)?.replaceFragment(EditProfileFragment())
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnHelp.setOnClickListener {
            // Navigate to help and support
            (activity as? MainActivity)?.replaceFragment(HelpSupportFragment())
        }

        // Remove change language and notification buttons as requested
        binding.btnLanguage.visibility = View.GONE
        binding.btnNotifications.visibility = View.GONE
    }
}