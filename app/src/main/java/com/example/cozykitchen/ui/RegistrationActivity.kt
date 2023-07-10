package com.example.cozykitchen.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.ActivityRegistrationBinding
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.model.User
import com.example.cozykitchen.request.PostUser
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide action bar
        supportActionBar?.hide()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)

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

        binding.tvLogin.setOnClickListener {
            toLoginActivity()
        }

        binding.btnRegister.setOnClickListener {
            registerNewUser()
        }

        setContentView(binding.root)
    }

    private fun registerNewUser() {
        var name = binding.etName.text.toString().trim()
        var email = binding.etEmail.text.toString().lowercase().trim()
        var password = binding.etPassword.text.toString().trim()
        var phoneNumber = binding.etPhoneNumber.text.toString().trim()
        var isNotValid = false

        // Validation
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
        } else if (!ValidatorSingleton.isValidPasswordLength(password)) {
            binding.etPassword.error = "Minimal 8 character needed"
            binding.etPassword.requestFocus()
            isNotValid = true
        }

        if (name.isEmpty()) {
            binding.etName.error = "Name required"
            binding.etName.requestFocus()
            isNotValid = true
        }

        if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "Mobile number required"
            binding.etPhoneNumber.requestFocus()
            isNotValid = true
        } else if (!ValidatorSingleton.isValidMalaysiaPhoneNumber(phoneNumber)) {
            binding.etPhoneNumber.error = "Invalid malaysia number"
            binding.etPhoneNumber.requestFocus()
            isNotValid = true
        }

        // The userId is a dummy text, it is needed for the POST Request,
        // it will not affect the id generated as the id generation is done
        // in the backend
        if(!isNotValid) {
            val user = PostUser(
                "test", name, email, password, phoneNumber
            )

            checkEmail(email, RequestBodySingleton.makeGSONRequestBody(user))
        }

    }

    private fun checkEmail(email: String, requestBody: RequestBody) {
        var checkedEmail = ""
        KitchenApi.retrofitService.getUsers().enqueue(object: Callback<List<User>?> {
            override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                val responseBody = response.body()

                if (responseBody != null) {
                    for (User in responseBody) {
                        if (User.userEmail == email) {
                            checkedEmail = User.userEmail
                        }
                    }
                }
                if (checkedEmail != email) {
                    createUser(requestBody)
                    Toast.makeText(this@RegistrationActivity, "Registered Successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RegistrationActivity, "Email Already Exists", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                Toast.makeText(this@RegistrationActivity, "Error", Toast.LENGTH_SHORT).show()
                Log.d("RegisterActivity", "${t.toString()}")
            }
        })
    }

    private fun createUser(requestBody: RequestBody) {
        KitchenApi.retrofitService.createUser(requestBody).enqueue(object: Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                Toast.makeText(this@RegistrationActivity, "Registered Successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(this@RegistrationActivity, "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun toLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}