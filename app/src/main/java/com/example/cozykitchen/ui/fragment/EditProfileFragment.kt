package com.example.cozykitchen.ui.fragment

import android.os.Bundle
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
import com.example.cozykitchen.databinding.FragmentEditProfileBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.model.User
import com.example.cozykitchen.request.PostUser
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
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

        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        session = LoginPreference(requireContext())
        userId = session.getCurrentUserId()

        KitchenApi.retrofitService.getUserById(userId).enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        binding.etName.setText(profile.userName)
                        binding.etEmail.setText(profile.userEmail)
                        binding.etPassword.setText(profile.userPassword)
                        binding.etPhoneNumber.setText(profile.userPhoneNumber)
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
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
                val user = PostUser(
                    "test", name, email, password, phoneNumber
                )

                updateProfile(userId, RequestBodySingleton.makeGSONRequestBody(user))
                // re-update the session
                session.createLoginSession(userId = userId, userEmail = email, userName = name, userPhoneNumber = phoneNumber)
            }
        }

    }

    private fun updateProfile(userId: String, requestBody: RequestBody) {
        KitchenApi.retrofitService.updateUser(userId, requestBody).enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    // return to previous screen
                    findNavController().navigateUp()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}