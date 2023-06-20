package com.example.cozykitchen.model

data class Card(
    val cardId: String?,
    val cardNumber: String?,
    val cardExpiryDateString: String?,
    val cvcCode: String?,
    val userId: String?,
)