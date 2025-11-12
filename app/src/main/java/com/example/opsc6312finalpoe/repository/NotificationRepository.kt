package com.example.opsc6312finalpoe.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import com.example.opsc6312finalpoe.models.Notification

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    suspend fun saveFCMToken(userId: String): Result<Boolean> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            db.collection("users").document(userId)
                .update("fcmToken", token)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(userId: String): List<Notification> {
        return try {
            notificationsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Notification::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun markAsRead(notificationId: String): Result<Boolean> {
        return try {
            notificationsCollection.document(notificationId)
                .update("read", true)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}