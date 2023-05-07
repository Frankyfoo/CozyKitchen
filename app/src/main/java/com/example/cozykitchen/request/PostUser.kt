package com.example.cozykitchen.request

data class PostUser(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPassword: String,
    val userPhoneNumber: String,
)