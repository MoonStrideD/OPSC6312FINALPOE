package com.example.opsc6312finalpoe.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.databinding.ActivitySettingsBinding
import com.example.opsc6312finalpoe.utils.LanguageHelper
import com.example.opsc6312finalpoe.utils.SharedPreferencesHelper

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme before setting content view
        applyThemeFromPreferences()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        setupUI()
        setupClickListeners()
    }

    private fun applyThemeFromPreferences() {
        val isDarkMode = SharedPreferencesHelper(this).isDarkMode()
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setupUI() {
        // Set current theme based on actual system setting, not just switch state
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeEnabled = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isDarkModeEnabled

        // Set current language
        val currentLanguage = sharedPreferencesHelper.getLanguage()
        when (currentLanguage) {
            "af" -> binding.radioAfrikaans.isChecked = true
            "zu" -> binding.radioZulu.isChecked = true
            else -> binding.radioEnglish.isChecked = true
        }

        // Set notification preference
        binding.switchNotifications.isChecked = sharedPreferencesHelper.areNotificationsEnabled()

        // Set offline mode preference
        binding.switchOfflineMode.isChecked = sharedPreferencesHelper.isOfflineMode()
    }

    private fun setupClickListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Apply theme change
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferencesHelper.setDarkMode(isChecked)

            // No need to recreate activity - let the system handle it
            // The activity will automatically recreate if needed
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEnglish -> changeLanguage("en")
                R.id.radioAfrikaans -> changeLanguage("af")
                R.id.radioZulu -> changeLanguage("zu")
            }
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesHelper.setNotificationsEnabled(isChecked)
        }

        binding.switchOfflineMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesHelper.setOfflineMode(isChecked)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun changeLanguage(languageCode: String) {
        sharedPreferencesHelper.saveLanguage(languageCode)
        LanguageHelper.setAppLocale(this, languageCode)
        recreate() // Only recreate for language changes
    }
}