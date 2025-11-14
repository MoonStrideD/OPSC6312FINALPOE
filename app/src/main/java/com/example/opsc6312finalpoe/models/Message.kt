package com.example.opsc6312finalpoe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "", // For direct messages, or "all" for broadcast
    val propertyId: String = "", // Optional: link to specific property
    val title: String = "",
    val content: String = "",
    val type: String = "notification", // notification, message, rent_reminder, etc.
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
) : Parcelable {

    fun getFormattedTime(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun getFormattedDate(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun isBroadcast(): Boolean {
        return receiverId == "all"
    }
}