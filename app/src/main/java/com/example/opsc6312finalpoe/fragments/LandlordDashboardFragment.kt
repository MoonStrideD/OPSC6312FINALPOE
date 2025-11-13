package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.View
import com.example.opsc6312finalpoe.databinding.FragmentLandlordDashboardBinding

class LandlordDashboardFragment : DashboardFragment() {
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

        // Hide tenant-specific buttons if they exist
        landlordBinding.btnBrowseProperties.visibility = View.GONE
    }
}