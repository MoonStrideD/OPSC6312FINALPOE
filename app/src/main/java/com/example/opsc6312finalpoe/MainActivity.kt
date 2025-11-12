package com.example.opsc6312finalpoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.activities.LoginActivity
import com.example.opsc6312finalpoe.databinding.ActivityMainBinding
import com.example.opsc6312finalpoe.fragments.DashboardFragment
import com.example.opsc6312finalpoe.fragments.ProfileFragment
import com.example.opsc6312finalpoe.fragments.PropertyListFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(this)
        offlineSyncManager = OfflineSyncManager(this)

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        // Check authentication
        checkAuthStatus()

        setupBottomNavigation()
        setupOfflineSync()
    }

    private fun checkAuthStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            val isLoggedIn = authRepository.isUserLoggedIn()
            if (!isLoggedIn) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    replaceFragment(DashboardFragment())
                    true
                }
                R.id.navigation_properties -> {
                    replaceFragment(PropertyListFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        replaceFragment(DashboardFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
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