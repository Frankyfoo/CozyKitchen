package com.example.cozykitchen.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.chef.activity.AddShopActivity
import com.example.cozykitchen.chef.activity.ChefMainActivity
import com.example.cozykitchen.databinding.ActivityChefLoginBinding
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChefLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChefLoginBinding
    private lateinit var session: LoginPreference
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create chef session
        session = LoginPreference(this)

        if (session.getCurrentUserId().startsWith("CHEF")) {
            val intent = Intent(this, ChefMainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // hide action bar
            supportActionBar?.hide()

            binding = ActivityChefLoginBinding.inflate(layoutInflater)

            // makes password visible and invisible
            binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Show the password
                    binding.etPassword.transformationMethod = null
                } else {
                    // Hide the password
                    binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
            }

            binding.tvToCustomerLogin.setOnClickListener {
                toCustomerLoginActivity()
            }

            binding.btnLogin.setOnClickListener {
                login()
            }

            setContentView(binding.root)
        }
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
            KitchenApi.retrofitService.getChefs().enqueue(object: Callback<List<Chef>>{
                override fun onResponse(call: Call<List<Chef>>, response: Response<List<Chef>>) {
                    if (response.isSuccessful) {
                        val chefs = response.body()
                        if (chefs != null) {
                            for (chef in chefs) {
                                if(chef.chefEmail == email && chef.chefPassword == password) {
                                    isLoggedIn = true
                                    Toast.makeText(this@ChefLoginActivity, "Login Successfully", Toast.LENGTH_SHORT).show()
                                    session.createLoginSession(chef.chefId, chef.chefEmail, chef.chefName, chef.chefPhoneNumber)
                                    chef.shopId?.let { session.createShopIdSession(it) }
                                    if (chef.shopId.isNullOrEmpty()) {
                                        var intent = Intent(this@ChefLoginActivity, ChefMainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else {
                                        var intent = Intent(this@ChefLoginActivity, ChefMainActivity::class.java)
                                        intent.putExtra("ShopId", chef.shopId)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                        if (!isLoggedIn) {
                            Toast.makeText(this@ChefLoginActivity, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Chef>>, t: Throwable) {
                    Toast.makeText(this@ChefLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun toCustomerLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}