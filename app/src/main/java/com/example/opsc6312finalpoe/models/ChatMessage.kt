package com.example.opsc6312finalpoe.models

data class ChatMessage(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageText: String = "",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "sent" // sent, delivered, read
)