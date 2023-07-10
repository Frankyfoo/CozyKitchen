package com.example.cozykitchen.chef.fragment

import android.content.Intent
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
import com.example.cozykitchen.adapter.ProductAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.chef.activity.AddShopActivity
import com.example.cozykitchen.databinding.FragmentMenuBinding
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.fragment.OnProductClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment : Fragment(), OnProductClickListener {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: ProductAdapter
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var fabAddFood: FloatingActionButton

    private var shopAvailable: Boolean = false
    private var shopId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Menu"

        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        fabAddFood = binding.fabAddFood

        foodRecyclerView = binding.menuRecyclerView
        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //get chef Id
        session = LoginPreference(requireContext())
        val chefId = session.getCurrentUserId()

        // get the ShopId of the current logged Chef
        KitchenApi.retrofitService.getChefById(chefId).enqueue(object: Callback<Chef> {
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if (response.isSuccessful) {
                    var chef = response.body()
                    if (chef != null) {
                        shopId = chef.shopId
                        shopAvailable = chef.shopId != null
                    }

                    if (shopId != null && shopAvailable) {
                        // shows foods if chef has shop id
                        KitchenApi.retrofitService.getFoodByShopId(shopId!!).enqueue(object: Callback<List<Product>>{
                            override fun onResponse(
                                call: Call<List<Product>>,
                                response: Response<List<Product>>
                            ) {
                                if (response.isSuccessful) {
                                    val foods = response.body()
                                    if (!foods.isNullOrEmpty()) {
                                        binding.tvNoFood.visibility = View.GONE
                                        adapter = ProductAdapter(foods, this@MenuFragment)
                                        foodRecyclerView.adapter = adapter
                                    } else {
                                        binding.tvNoFood.visibility = View.VISIBLE
                                    }
//                                    Log.d("Testing", "$foods")
                                }
                            }

                            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                                Log.d("Failed", "${t.message}")
                            }

                        })
                    } else {
                        // shows that chef has not created a shop yet
                        binding.tvNoShop.visibility = View.VISIBLE
                        binding.fabAddFood.visibility = View.GONE
                        binding.tvNoShop.setOnClickListener {
                            val intent = Intent(requireContext(), AddShopActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Log.d("Failed", "${t.message}")
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Todo: Add FAB function to open new Fragment for add Food
        fabAddFood.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("ShopId", shopId)
            findNavController().navigate(R.id.action_menuFragment_to_manageFoodFragment, bundle)
        }

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        // Hide the back button in the app bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onItemClick(product: Product) {
        val bundle = Bundle()
        bundle.putBoolean("IsCardClick", true)
        bundle.putString("ProductId", product.productId)
        findNavController().navigate(R.id.action_menuFragment_to_manageFoodFragment, bundle)
    }
}