package com.example.cozykitchen.model

data class Product (
    val productId: String,
    val productName: String,
    val productDescription: String,
    val productImageUrl: String,
    val productPrice: Float,
    val shopId: String,
)

