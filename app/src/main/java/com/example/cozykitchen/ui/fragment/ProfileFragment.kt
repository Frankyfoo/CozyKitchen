package com.example.cozykitchen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.FragmentProfileBinding
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
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
        binding.btnLogout.setOnClickListener {
            session.LogoutUser()
        }

        binding.btnManageAddress.setOnClickListener {
            findNavController().navigate(R.id.action_profile_fragment_to_addressListFragment)
        }

        binding.btnManagePayment.setOnClickListener {
            findNavController().navigate(R.id.action_profile_fragment_to_cardListFragment)
        }

        binding.btnManageWallet.setOnClickListener {
            findNavController().navigate(R.id.action_profile_fragment_to_walletListFragment)
        }
    }
}