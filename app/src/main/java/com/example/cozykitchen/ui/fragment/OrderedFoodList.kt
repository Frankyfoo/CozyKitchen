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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.CartAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentOrderedFoodListBinding
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderedFoodList : Fragment() {

    private lateinit var binding: FragmentOrderedFoodListBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Ordered Food"

        val orderId = arguments?.getString("OrderId")
        Log.d("Testing", orderId!!)

        binding = FragmentOrderedFoodListBinding.inflate(inflater, container, false)
        val orderedRecyclerView: RecyclerView = binding.rvOrderedFood
        orderedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getShoppingCartListByOrderId(orderId!!).enqueue(object: Callback<List<ShoppingCart>>{
            override fun onResponse(
                call: Call<List<ShoppingCart>>,
                response: Response<List<ShoppingCart>>
            ) {
                if(response.isSuccessful) {
                    val body = response.body()
                    if(!body.isNullOrEmpty()) {
                        adapter = CartAdapter(body)
                        orderedRecyclerView.adapter = adapter
                        Log.d("testingOrder", "$body")
                    }
                }
            }

            override fun onFailure(call: Call<List<ShoppingCart>>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
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
    }
}