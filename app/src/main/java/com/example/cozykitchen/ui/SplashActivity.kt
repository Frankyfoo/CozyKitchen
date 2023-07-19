package com.example.cozykitchen.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
//            startActivity(Intent(this, LoginActivity::class.java))

            // Check location permission status
            if (checkLocationPermission()) {
                // Location permission granted, start LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Location permission not granted, start PermissionActivity
                startActivity(Intent(this, PermissionRequestActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }
}