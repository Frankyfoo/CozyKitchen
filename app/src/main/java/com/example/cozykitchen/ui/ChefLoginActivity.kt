package com.example.cozykitchen.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cozykitchen.databinding.ActivityChefLoginBinding

class ChefLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChefLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide action bar
        supportActionBar?.hide()

        binding = ActivityChefLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvToCustomerLogin.setOnClickListener {
            toCustomerLoginActivity()
        }
    }

    private fun toCustomerLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}