package com.example.opsc6312finalpoe.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.opsc6312finalpoe.R

object NotificationHelper {
    private const val CHANNEL_ID = "breezynest_channel"
    private const val CHANNEL_NAME = "BreezyNest Notifications"

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

    fun showRentReminder(context: Context, propertyName: String, daysUntilDue: Int) {
        val title = context.getString(R.string.rent_reminder)
        val message = context.getString(R.string.rent_due_soon, propertyName, daysUntilDue)
        showNotification(context, title, message)
    }

    fun showNewPropertyNotification(context: Context, location: String) {
        val title = context.getString(R.string.new_property_available)
        val message = context.getString(R.string.new_property_in_location, location)
        showNotification(context, title, message)
    }
}