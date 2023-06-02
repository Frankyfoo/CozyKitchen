package com.example.cozykitchen.ui.fragment

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
import com.example.cozykitchen.databinding.FragmentFoodDetailBinding
import com.example.cozykitchen.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentFoodDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // getting data from previous screen
        val productId = arguments?.getString("ProductId")
        val productName = arguments?.getString("ProductName")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = productName

        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        if (productId != null) {
            KitchenApi.retrofitService.getFoodById(productId).enqueue(object: Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    val responseBody = response.body()
                    // Code is working
                    Log.d("FoodDetailTesting", "$responseBody")
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }
}