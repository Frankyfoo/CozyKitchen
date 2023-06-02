package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.ProductAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentFoodListBinding
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.model.Shop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnProductClickListener {
    fun onItemClick(product: Product)
}

class FoodListFragment : Fragment(), OnProductClickListener {

    private lateinit var binding: FragmentFoodListBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // getting data from previous screen
        val shopId = arguments?.getString("ShopId")
        val shopName = arguments?.getString("ShopName")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = shopName

        // set up binding
        binding = FragmentFoodListBinding.inflate(inflater, container, false)
        val foodRecyclerView: RecyclerView = binding.foodRecyclerView
        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Api Call and RecyclerView Populated
        if (shopId != null) {
            KitchenApi.retrofitService.getFoodByShopId(shopId).enqueue(object: Callback<List<Product>?> {
                override fun onResponse(
                    call: Call<List<Product>?>,
                    response: Response<List<Product>?>
                ) {
                    val products = response.body()

                    if (products != null && products.isNotEmpty()) {
                        adapter = ProductAdapter(products, this@FoodListFragment)
                        foodRecyclerView.adapter = adapter
                        binding.noProductsTextView.visibility = View.GONE
                    } else {
                        binding.noProductsTextView.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                    Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
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

    override fun onItemClick(product: Product) {
        val bundle = Bundle()
        bundle.apply {
            putString("ProductId", product.productId)
            putString("ProductName", product.productName)
        }

        findNavController().navigate(R.id.action_foodListFragment_to_foodDetailFragment, bundle)
    }
}