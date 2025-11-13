package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.View
import com.example.opsc6312finalpoe.databinding.FragmentTenantDashboardBinding

class TenantDashboardFragment : DashboardFragment() {
    private lateinit var tenantBinding: FragmentTenantDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        tenantBinding = FragmentTenantDashboardBinding.inflate(inflater, container, false)
        return tenantBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTenantSpecificUI()
    }

    private fun setupTenantSpecificUI() {
        // Tenant-specific buttons and functionality
        tenantBinding.btnBrowseProperties.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(PropertyListFragment())
        }

        tenantBinding.btnViewApplications.setOnClickListener {
            // Navigate to tenant applications
        }

        // Hide landlord-specific buttons if they exist
        tenantBinding.btnAddProperty.visibility = View.GONE
    }
}