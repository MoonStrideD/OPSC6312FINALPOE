package com.example.opsc6312finalpoe.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment() {
    private lateinit var binding: FragmentHelpSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:developer@breezynest.com")
                putExtra(Intent.EXTRA_SUBJECT, "BreezyNest Support")
                putExtra(Intent.EXTRA_TEXT, "Hello BreezyNest team,\n\nI need help with:")
            }
            startActivity(intent)
        }

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+1234567890")
            }
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}