package com.example.cozykitchen.model

data class Product (
    val productId: String,
    var productName: String,
    var productDescription: String,
    var productIngredients: String,
    var productUrl: String,
    var productPrice: Float,
    var productIsAvailable: Boolean,
    val shopId: String,
)

