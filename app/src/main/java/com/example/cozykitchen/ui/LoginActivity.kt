package com.example.cozykitchen.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.chef.activity.ChefMainActivity
import com.example.cozykitchen.databinding.ActivityLoginBinding
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.model.TimeList
import com.example.cozykitchen.model.User
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.sharedPreference.LoginPreference.Companion.KEY_USERID
import com.example.cozykitchen.ui.fragment.ShopFragment
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

        // Request location permission
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)

        //session.getUserDetails()[KEY_USERID]?.startsWith("CUS") == true
        // if user has logged in before and did not press log out, it will go the MainActivity instead
        if (session.getCurrentUserId().startsWith("CUS")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else if (session.getCurrentUserId().startsWith("CHEF")) {
            val intent = Intent(this, ChefMainActivity::class.java)
            startActivity(intent)
        } else {
            // hide action bar
            supportActionBar?.hide()

            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.tvRegister.setOnClickListener {
                toRegisterActivity()
            }

            binding.tvToChefLogin.setOnClickListener {
                toChefLoginActivity()
            }

            binding.btnLogin.setOnClickListener {
                login()
            }
        }

        // Close the application when back button is pressed
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun toChefLoginActivity() {
        val intent = Intent(this, ChefLoginActivity::class.java)
        startActivity(intent)
    }

    private fun login() {
        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()
        var isNotValid = false

        if (email.isEmpty()) {
            binding.etEmail.error = "Email required"
            binding.etEmail.requestFocus()
            isNotValid = true
        } else if (!ValidatorSingleton.isValidEmail(email)) {
            binding.etEmail.error = "Incorrect Email Address"
            binding.etEmail.requestFocus()
            isNotValid = true
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            binding.etPassword.requestFocus()
            isNotValid = true
        } else if (!ValidatorSingleton.isValidPasswordLength(password)){
            binding.etPassword.error = "Minimal 8 character needed"
            binding.etPassword.requestFocus()
            isNotValid = true
        }

        if(!isNotValid) {
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
    }

    private fun toRegisterActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}