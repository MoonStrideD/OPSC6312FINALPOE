package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.databinding.FragmentDashboardBinding

open class DashboardFragment : Fragment() {
    protected lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommonUI()
    }

    protected open fun setupCommonUI() {
        // Common dashboard setup (logo, welcome message, etc.)
        // Add your logo here
        binding.ivLogo.visibility = View.VISIBLE
        binding.ivLogo.setImageResource(R.drawable.logo)
    }
}