package com.example.cozykitchen.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.ActivityLoginBinding
import com.example.cozykitchen.model.User
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: LoginPreference
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = LoginPreference(this)

        // hide action bar
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            toRegisterActivity()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email required"
            binding.etEmail.requestFocus()
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            binding.etPassword.requestFocus()
        }

        KitchenApi.retrofitService.getUsers().enqueue(object: Callback<List<User>?> {
            override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                val responseBody = response.body()

                if (responseBody != null) {
                    for (User in responseBody) {
                        if (User.userEmail == email && User.userPassword == password) {
                            isLoggedIn = true
                            Toast.makeText(this@LoginActivity, "Login Successfully", Toast.LENGTH_SHORT).show()
                            session.createLoginSession(User.userId, User.userEmail, User.userName, User.userPhoneNumber)
                            var intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("userName", User.userName)
                            intent.putExtra("userEmail", User.userEmail)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                if (!isLoggedIn) {
                    Toast.makeText(this@LoginActivity, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun toRegisterActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}