package com.example.cozykitchen.model

data class Summary(
    val orderId: String,
    val deliveryDateTimeString: String,
    val shopId: String,
    val size: String,
    val productId: String,
    val quantityBought: String,
    val customizationDescription: String,
    val productName: String,
    val productUrl: String
)