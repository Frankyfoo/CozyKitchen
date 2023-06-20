package com.example.cozykitchen.request

data class PostCard(
    val cardId: String,
    val cardNumber: String,
    val cardExpiryDateString: String,
    val cvcCode: String,
    val userId: String,
)