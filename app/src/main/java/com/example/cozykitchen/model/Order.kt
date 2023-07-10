package com.example.cozykitchen.model

import java.time.LocalDateTime

data class Order(
    val orderId: String,
    val totalPrice: Float,
    val status: String,
    val paymentType: String,
    val remarks: String?,
    val userId: String,
    val addressId: String,
    val cardId: String?,
    val walletId: String?,
    val shopId: String,
    val deliveryDateTime: String,
    val deliveryDateTimeString: String,
    var deliveryLocalDateTime: LocalDateTime,
//    val user: User,
//    val address: Address,
//    val card: Card,
)