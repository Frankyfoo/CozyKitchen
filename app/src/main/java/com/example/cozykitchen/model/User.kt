package com.example.cozykitchen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val userName: String,
    val userEmail: String,
    // if using convertToUserObject in LoginPreference, userPassword must be set to String?
    val userPassword: String,
    val userPhoneNumber: String
): Parcelable