package com.example.opsc6312finalpoe.models

data class Notification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "", // rent_reminder, new_listing, application_update
    val read: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)