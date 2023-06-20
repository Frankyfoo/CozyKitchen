package com.example.cozykitchen.model

data class ShoppingCart(
    val shoppingCartId: String,
    val userId: String,
    val productId: String,
    val size: String,
    val quantityBought: Int,
    val customizationDescription: String?,
    val deliveryDateTimeString: String,
    val status: String,
    val orderId: String?,
    val product: Product?
)