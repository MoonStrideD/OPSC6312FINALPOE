package com.example.opsc6312finalpoe.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.opsc6312finalpoe.utils.NotificationHelper

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token: $token")
        // Send token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received: ${remoteMessage.data}")

        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"] ?: "BreezyNest"
            val message = remoteMessage.data["message"] ?: "New notification"

            NotificationHelper.showNotification(applicationContext, title, message)
        }
    }
}