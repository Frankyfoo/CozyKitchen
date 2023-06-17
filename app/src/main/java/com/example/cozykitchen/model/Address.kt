package com.example.cozykitchen.model

data class Address(
    val addressId: String,
    val name: String,
    val street: String,
    val streetNumber: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val instruction: String,
    val userId: String,
)