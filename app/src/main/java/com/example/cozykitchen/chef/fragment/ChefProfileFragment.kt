package com.example.cozykitchen.chef.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentChefProfileBinding
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val chefId = session.getCurrentUserId()

        binding = FragmentChefProfileBinding.inflate(inflater, container, false)

        KitchenApi.retrofitService.getChefById(chefId).enqueue(object: Callback<Chef>{
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if(response.isSuccessful) {
                    val chef = response.body()
                    if (chef != null) {
                        if (chef.shopId.isNullOrEmpty()) {
                            binding.btnEditShop.visibility = View.GONE
                        } else {
                            binding.btnEditShop.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

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

        // edit
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_chefProfileFragment_to_chefEditProfileFragment)
        }

        // edit shop
        binding.btnEditShop.setOnClickListener {
            findNavController().navigate(R.id.action_chefProfileFragment_to_editShopFragment)
        }

        // logs out user
        binding.btnChefLogout.setOnClickListener {
            session.LogoutUser()
        }
    }
}