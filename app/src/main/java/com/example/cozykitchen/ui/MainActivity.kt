package com.example.cozykitchen.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cozykitchen.R
import com.example.cozykitchen.sharedPreference.LoginPreference

class MainActivity : AppCompatActivity() {

    private lateinit var session: LoginPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session = LoginPreference(this)

        // working
        Log.d("MainTesting", "${session.getUserDetails()}")
    }
}