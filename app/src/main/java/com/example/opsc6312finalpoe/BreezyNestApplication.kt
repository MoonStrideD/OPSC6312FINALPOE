package com.example.opsc6312finalpoe

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.opsc6312finalpoe.utils.SharedPreferencesHelper

class BreezyNestApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val sharedPreferencesHelper = SharedPreferencesHelper(this)

        // Set theme based on saved preference
        if (sharedPreferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Set language based on saved preference
        val languageCode = sharedPreferencesHelper.getLanguage()
        // Language will be set when activities are created
    }
}