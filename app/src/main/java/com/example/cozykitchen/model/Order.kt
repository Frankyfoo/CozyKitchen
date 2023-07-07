package com.example.cozykitchen.model

data class Order(
    val orderId: String,
    val totalPrice: Float,
    val status: String,
    val paymentType: String,
    val remarks: String?,
    val deliveryDateTimeString: String,
    val user: User,
    val address: Address,
    val card: Card,
)