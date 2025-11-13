package com.example.opsc6312finalpoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.activities.LoginActivity
import com.example.opsc6312finalpoe.databinding.ActivityMainBinding
import com.example.opsc6312finalpoe.fragments.*
import com.example.opsc6312finalpoe.repository.AuthRepository
import com.example.opsc6312finalpoe.utils.NotificationHelper
import com.example.opsc6312finalpoe.utils.OfflineSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var offlineSyncManager: OfflineSyncManager
    private var userRole: String = "tenant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(this)
        offlineSyncManager = OfflineSyncManager(this)

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        // Check authentication and load user data
        checkAuthStatusAndLoadUser()

        setupBottomNavigation()
        setupOfflineSync()
    }

    private fun checkAuthStatusAndLoadUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val isLoggedIn = authRepository.isUserLoggedIn()
            if (!isLoggedIn) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Load user data to determine role
                val user = authRepository.getCurrentUserData()
                userRole = user?.role ?: "tenant"
                setupRoleBasedUI()
            }
        }
    }

    private fun setupRoleBasedUI() {
        // You can customize UI based on user role here
        when (userRole) {
            "landlord" -> {
                // Landlord specific setup
                replaceFragment(LandlordDashboardFragment())
            }
            else -> {
                // Tenant specific setup
                replaceFragment(TenantDashboardFragment())
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    when (userRole) {
                        "landlord" -> replaceFragment(LandlordDashboardFragment())
                        else -> replaceFragment(TenantDashboardFragment())
                    }
                    true
                }
                R.id.navigation_properties -> {
                    replaceFragment(PropertyListFragment())
                    true
                }
                R.id.navigation_favorites -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.navigation_chat -> {
                    replaceFragment(ChatFragment())
                    true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(NotificationsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Set default fragment based on role
        when (userRole) {
            "landlord" -> replaceFragment(LandlordDashboardFragment())
            else -> replaceFragment(TenantDashboardFragment())
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupOfflineSync() {
        CoroutineScope(Dispatchers.IO).launch {
            offlineSyncManager.schedulePeriodicSync()
        }
    }

    override fun onResume() {
        super.onResume()
        // Sync data when app comes to foreground
        CoroutineScope(Dispatchers.IO).launch {
            offlineSyncManager.syncProperties()
        }
    }
}