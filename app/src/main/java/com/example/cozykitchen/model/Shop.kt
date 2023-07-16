package com.example.cozykitchen.model

data class Shop(
    val shopId: String,
    var shopName: String,
    var shopDescription: String,
    var shopImageUrl: String,
    var latitude: Double,
    var longitude: Double,
    var distance: Float?
)