package com.example.opsc6312finalpoe.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.opsc6312finalpoe.models.ChatMessage

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    suspend fun sendMessage(chatMessage: ChatMessage): Result<Boolean> {
        return try {
            chatsCollection.document(chatMessage.chatId)
                .collection("messages")
                .document(chatMessage.messageId)
                .set(chatMessage)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(chatId: String): List<ChatMessage> {
        return try {
            chatsCollection.document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(ChatMessage::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getChatsForUser(userId: String): List<String> {
        return try {
            val sentChats = chatsCollection
                .whereEqualTo("senderId", userId)
                .get()
                .await()
                .documents
                .map { it.id }

            val receivedChats = chatsCollection
                .whereEqualTo("receiverId", userId)
                .get()
                .await()
                .documents
                .map { it.id }

            (sentChats + receivedChats).distinct()
        } catch (e: Exception) {
            emptyList()
        }
    }
}