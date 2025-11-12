package com.example.opsc6312finalpoe.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("BreezyNestPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "preferred_language"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_OFFLINE_MODE = "offline_mode"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    // Language preferences
    fun saveLanguage(languageCode: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    // User preferences
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    // Notification preferences
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    // Offline mode
    fun setOfflineMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
    }

    fun isOfflineMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_OFFLINE_MODE, false)
    }

    // Dark mode preferences
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    // Clear all preferences (logout)
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}