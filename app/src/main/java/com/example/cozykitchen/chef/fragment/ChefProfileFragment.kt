package com.example.cozykitchen.chef.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.FragmentChefProfileBinding
import com.example.cozykitchen.sharedPreference.LoginPreference

class ChefProfileFragment : Fragment() {

    private lateinit var binding: FragmentChefProfileBinding
    private lateinit var session: LoginPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        session = LoginPreference(requireContext())
        binding = FragmentChefProfileBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        // Hide the back button in the app bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // logs out user
        binding.btnChefLogout.setOnClickListener {
            session.LogoutUser()
        }
    }
}