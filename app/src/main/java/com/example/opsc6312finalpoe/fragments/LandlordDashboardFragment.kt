package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.MainActivity
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.databinding.FragmentLandlordDashboardBinding
import com.example.opsc6312finalpoe.utils.NotificationHelper
import com.google.android.material.snackbar.Snackbar

class LandlordDashboardFragment : Fragment() {
    private lateinit var landlordBinding: FragmentLandlordDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        landlordBinding = FragmentLandlordDashboardBinding.inflate(inflater, container, false)
        return landlordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLandlordSpecificUI()
        setupNotificationTesting()
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

    private fun setupNotificationTesting() {
        // Initialize notification channel
        NotificationHelper.createNotificationChannel(requireContext())

        // Find the test button by ID and set up click listener
        val testButton = landlordBinding.root.findViewById<View>(R.id.btnTestNotifications)
        testButton?.setOnClickListener {
            testAllNotifications()
        }

        // If you have individual test buttons, set them up too
        setupIndividualTestButtons()
    }

    private fun setupIndividualTestButtons() {
        // Rent reminder test button
        landlordBinding.root.findViewById<View?>(R.id.btnTestRent)?.setOnClickListener {
            testRentReminder()
        }

        // New property test button
        landlordBinding.root.findViewById<View?>(R.id.btnTestProperty)?.setOnClickListener {
            testNewPropertyNotification()
        }

        // Quick test button
        landlordBinding.root.findViewById<View?>(R.id.btnQuickTest)?.setOnClickListener {
            quickNotificationTest()
        }
    }

    // Test all notification types at once
    private fun testAllNotifications() {
        try {
            val currentUserId = "landlord_123" // Replace with actual landlord ID
            val landlordName = "Property Manager" // Replace with actual landlord name

            NotificationHelper.createNotificationChannel(requireContext())

            // Test 1: Basic notification with message
            NotificationHelper.sendCustomMessageToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "BreezyNest Test ✅",
                "Basic notification system is working perfectly! You'll see this message in your messages screen."
            )

            // Test 2: Rent reminder with message - USE CORRECT METHOD NAME
            NotificationHelper.sendRentReminderToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "Luxury Apartment",
                5,
                "property_2" // Example property ID
            )

            // Test 3: New property with message - USE CORRECT METHOD NAME
            NotificationHelper.sendNewPropertyToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "Sandton Area",
                "property_1" // Example property ID
            )

            // Show success message
            Snackbar.make(landlordBinding.root, "🎉 3 test notifications sent! Tenants will see these as messages.", Snackbar.LENGTH_LONG).show()

            // Log for debugging
            Log.d("LandlordDashboard", "All test notifications and messages sent successfully")

        } catch (e: Exception) {
            Log.e("LandlordDashboard", "Error testing notifications", e)
            Snackbar.make(landlordBinding.root, "❌ Error: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    // Test rent reminder specifically - USE CORRECT METHOD NAME
    private fun testRentReminder() {
        try {
            val currentUserId = "landlord_123"
            val landlordName = "Property Manager"

            NotificationHelper.sendRentReminderToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "Modern Apartment",
                3,
                "property_3"
            )
            Snackbar.make(landlordBinding.root, "💰 Rent reminder notification and message sent!", Snackbar.LENGTH_SHORT).show()
            Log.d("LandlordDashboard", "Rent reminder test completed")
        } catch (e: Exception) {
            Log.e("LandlordDashboard", "Error testing rent reminder", e)
        }
    }

    // Test new property notification - USE CORRECT METHOD NAME
    private fun testNewPropertyNotification() {
        try {
            val currentUserId = "landlord_123"
            val landlordName = "Property Manager"

            NotificationHelper.sendNewPropertyToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "Cape Town",
                "property_4"
            )
            Snackbar.make(landlordBinding.root, "🏠 New property notification and message sent!", Snackbar.LENGTH_SHORT).show()
            Log.d("LandlordDashboard", "New property test completed")
        } catch (e: Exception) {
            Log.e("LandlordDashboard", "Error testing new property notification", e)
        }
    }

    // Quick single notification test
    private fun quickNotificationTest() {
        try {
            val currentUserId = "landlord_123"
            val landlordName = "Property Manager"

            NotificationHelper.sendCustomMessageToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                "Quick Test 🚀",
                "Your notification and messaging system is working great! This message will appear in the messages screen."
            )
            Snackbar.make(landlordBinding.root, "🔔 Quick test notification and message sent!", Snackbar.LENGTH_SHORT).show()
            Log.d("LandlordDashboard", "Quick notification test completed")
        } catch (e: Exception) {
            Log.e("LandlordDashboard", "Error in quick notification test", e)
        }
    }

    // Simple method to test one specific notification
    private fun testSingleNotification(title: String, message: String) {
        try {
            val currentUserId = "landlord_123"
            val landlordName = "Property Manager"

            NotificationHelper.sendCustomMessageToTenants(
                requireContext(),
                currentUserId,
                landlordName,
                title,
                message
            )
            Snackbar.make(landlordBinding.root, "Notification sent: $title", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("LandlordDashboard", "Error sending single notification", e)
        }
    }

    // Optional: Auto-test when fragment becomes visible
    override fun onResume() {
        super.onResume()
        // Uncomment the line below if you want to auto-test when dashboard opens
        // autoTestOnOpen()
    }

    private fun autoTestOnOpen() {
        if (shouldAutoTest()) {
            testAllNotifications()
        }
    }

    private fun shouldAutoTest(): Boolean {
        // Return false to disable auto-testing
        // Set to true only during development
        return false
    }
}