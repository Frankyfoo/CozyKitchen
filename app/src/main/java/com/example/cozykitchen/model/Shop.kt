package com.example.cozykitchen.model

data class Shop(
    val shopId: String,
    val shopName: String,
    val shopDescription: String,
    val shopImageUrl: String,
    val latitude: Double,
    val longitude: Double,
    var distance: Float?
)