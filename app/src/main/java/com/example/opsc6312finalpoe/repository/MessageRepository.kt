package com.example.opsc6312finalpoe.repository

import com.example.opsc6312finalpoe.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MessageRepository {
    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("messages")

    suspend fun sendMessage(message: Message): Result<Boolean> {
        return try {
            val messageWithId = message.copy(messageId = UUID.randomUUID().toString())
            messagesCollection.document(messageWithId.messageId)
                .set(messageWithId)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendBroadcastToTenants(
        senderId: String,
        senderName: String,
        title: String,
        content: String,
        propertyId: String = ""
    ): Result<Boolean> {
        val broadcastMessage = Message(
            senderId = senderId,
            senderName = senderName,
            receiverId = "all", // Broadcast to all tenants
            propertyId = propertyId,
            title = title,
            content = content,
            type = "broadcast",
            timestamp = System.currentTimeMillis()
        )
        return sendMessage(broadcastMessage)
    }

    suspend fun getMessagesForUser(userId: String): List<Message> {
        return try {
            // Get messages specifically for this user OR broadcast messages (receiverId = "all")
            messagesCollection
                .whereEqualTo("receiverId", userId)
                .get()
                .await()
                .toObjects(Message::class.java) +
                    messagesCollection
                        .whereEqualTo("receiverId", "all")
                        .get()
                        .await()
                        .toObjects(Message::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getChatsForUser(userId: String): List<Message> {
        return try {
            val messages = getMessagesForUser(userId)
            // Group by sender and get the latest message from each sender
            messages.groupBy { it.senderId }
                .map { (_, messages) -> messages.maxByOrNull { it.timestamp } }
                .filterNotNull()
                .sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun markAsRead(messageId: String): Result<Boolean> {
        return try {
            messagesCollection.document(messageId)
                .update("read", true)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}