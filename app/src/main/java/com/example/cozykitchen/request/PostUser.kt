package com.example.cozykitchen.request

// This has the same data types as User, It may not be used
data class PostUser(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPassword: String,
    val userPhoneNumber: String,
)