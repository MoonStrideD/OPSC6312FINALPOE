package com.example.opsc6312finalpoe.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationHelper {
    private const val CHANNEL_ID = "breezynest_channel"
    private const val CHANNEL_NAME = "BreezyNest Notifications"
    private val messageRepository = MessageRepository()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for property updates, rent reminders, and messages"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    // Send rent reminder notification AND message to tenants
    fun sendRentReminderToTenants(
        context: Context,
        senderId: String,
        senderName: String,
        propertyName: String,
        daysUntilDue: Int,
        propertyId: String = ""
    ) {
        val title = "Rent Reminder"
        val message = "Your rent for $propertyName is due in $daysUntilDue days"

        // Show local notification
        showNotification(context, title, message)

        // Send message to all tenants
        CoroutineScope(Dispatchers.IO).launch {
            messageRepository.sendBroadcastToTenants(
                senderId = senderId,
                senderName = senderName,
                title = title,
                content = message,
                propertyId = propertyId
            )
        }
    }

    // Send new property notification AND message to tenants
    fun sendNewPropertyToTenants(
        context: Context,
        senderId: String,
        senderName: String,
        location: String,
        propertyId: String = ""
    ) {
        val title = "New Property Available"
        val message = "A new property just listed in $location area"

        // Show local notification
        showNotification(context, title, message)

        // Send message to all tenants
        CoroutineScope(Dispatchers.IO).launch {
            messageRepository.sendBroadcastToTenants(
                senderId = senderId,
                senderName = senderName,
                title = title,
                content = message,
                propertyId = propertyId
            )
        }
    }

    // Send custom broadcast message to tenants
    fun sendCustomMessageToTenants(
        context: Context,
        senderId: String,
        senderName: String,
        title: String,
        message: String,
        propertyId: String = ""
    ) {
        // Show local notification
        showNotification(context, title, message)

        // Send message to all tenants
        CoroutineScope(Dispatchers.IO).launch {
            messageRepository.sendBroadcastToTenants(
                senderId = senderId,
                senderName = senderName,
                title = title,
                content = message,
                propertyId = propertyId
            )
        }
    }
}