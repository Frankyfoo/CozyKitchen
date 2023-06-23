package com.example.cozykitchen.model

data class Order(
    val orderId: String,
    val totalPrice: Float,
    val status: String,
    val user: User,
    val address: Address,
    val card: Card,
)