package com.example.cozykitchen.chef.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentChefEditProfileBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.request.PostUser
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChefEditProfileFragment : Fragment() {

    private lateinit var binding: FragmentChefEditProfileBinding
    private lateinit var session: LoginPreference

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Edit Profile"

        binding = FragmentChefEditProfileBinding.inflate(layoutInflater, container, false)
        session = LoginPreference(requireContext())
        userId = session.getCurrentUserId()

        KitchenApi.retrofitService.getChefById(userId).enqueue(object: Callback<Chef>{
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        binding.etName.setText(profile.chefName)
                        binding.etEmail.setText(profile.chefEmail)
                        binding.etPassword.setText(profile.chefPassword)
                        binding.etPhoneNumber.setText(profile.chefPhoneNumber)
                    }
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.btnSaveProfile.setOnClickListener {
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
                val chef = Chef(
                    "test", name, password, email, phoneNumber, null
                )

                updateProfile(userId, RequestBodySingleton.makeGSONRequestBody(chef))
                // re-update the session
                session.createLoginSession(userId = userId, userEmail = email, userName = name, userPhoneNumber = phoneNumber)
            }
        }
    }

    private fun updateProfile(userId: String, requestBody: RequestBody) {
        KitchenApi.retrofitService.updateChef(userId, requestBody).enqueue(object: Callback<Chef>{
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    // return to previous screen
                    findNavController().navigateUp()
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}