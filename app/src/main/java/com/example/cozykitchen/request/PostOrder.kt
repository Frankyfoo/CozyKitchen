package com.example.cozykitchen.request

data class PostOrder(
    val orderId: String,
    val totalPrice: Float,
    val status: String,
    val userId: String,
    val addressId: String,
    val cardId: String?,
    val walletId: String?,
    val paymentType: String,
    val remarks: String?,
    val deliveryDateTimeString: String,
    val shopId: String?,
)