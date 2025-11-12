package com.example.opsc6312finalpoe.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.opsc6312finalpoe.databinding.ActivitySettingsBinding
import com.example.opsc6312finalpoe.utils.LanguageHelper
import com.example.opsc6312finalpoe.utils.SharedPreferencesHelper

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set current theme
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.switchDarkMode.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

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
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferencesHelper.setDarkMode(isChecked)
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
        val newContext = LanguageHelper.setAppLocale(this, languageCode)

        // Restart activity to apply language changes
        recreate()
    }
}