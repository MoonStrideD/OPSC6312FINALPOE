package com.example.opsc6312finalpoe.models

data class Payment(
    val paymentId: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val amount: Double = 0.0,
    val paymentType: String = "", // rent, deposit
    val paymentGateway: String = "", // payfast, stripe
    val transactionRef: String = "",
    val status: String = "", // pending, completed, failed
    val receiptUrl: String = "",
    val paymentDate: Long = System.currentTimeMillis()
)