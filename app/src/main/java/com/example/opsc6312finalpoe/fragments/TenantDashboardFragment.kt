package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.MainActivity
import com.example.opsc6312finalpoe.databinding.FragmentTenantDashboardBinding

class TenantDashboardFragment : Fragment() {
    private lateinit var tenantBinding: FragmentTenantDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            // (activity as? MainActivity)?.replaceFragment(TenantApplicationsFragment())
        }
    }
}