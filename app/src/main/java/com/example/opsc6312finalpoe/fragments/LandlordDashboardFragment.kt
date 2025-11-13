package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.MainActivity
import com.example.opsc6312finalpoe.databinding.FragmentLandlordDashboardBinding

class LandlordDashboardFragment : Fragment() {
    private lateinit var landlordBinding: FragmentLandlordDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        landlordBinding = FragmentLandlordDashboardBinding.inflate(inflater, container, false)
        return landlordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLandlordSpecificUI()
    }

    private fun setupLandlordSpecificUI() {
        // Landlord-specific buttons and functionality
        landlordBinding.btnAddProperty.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(AddPropertyFragment())
        }

        landlordBinding.btnViewProperties.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(PropertyListFragment())
        }
    }
}