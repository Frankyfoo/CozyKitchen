package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    private lateinit var etSearchFood: EditText

    private var shopId: String? = null

    private var searchQuery: String = ""
    private var products: MutableList<Product>? = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // getting data from previous screen
        shopId = arguments?.getString("ShopId")
        val shopName = arguments?.getString("ShopName")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = shopName

        // set up binding
        binding = FragmentFoodListBinding.inflate(inflater, container, false)
        val foodRecyclerView: RecyclerView = binding.foodRecyclerView
        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        etSearchFood = binding.etSearchFood
        etSearchFood.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the search query
                searchQuery = s.toString().trim()

                // Filter the products based on the search query
                val filteredProducts = filterProducts(searchQuery)

                // Update the adapter with the filtered products
                adapter.updateProductList(filteredProducts, searchQuery)

                // Show/hide the "No Products" text based on the filtered products
                binding.noProductsTextView.visibility = if (filteredProducts.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })

        // Api Call and RecyclerView Populated
        if (shopId != null) {
            KitchenApi.retrofitService.getFoodByShopId(shopId!!).enqueue(object: Callback<List<Product>?> {
                override fun onResponse(
                    call: Call<List<Product>?>,
                    response: Response<List<Product>?>
                ) {
                    products = response.body() as MutableList<Product>?

                    if (products != null && products!!.isNotEmpty()) {
                        binding.noProductsTextView.visibility = View.GONE
                        binding.etSearchFood.visibility = View.VISIBLE
                        adapter = ProductAdapter(products!!, this@FoodListFragment)
                        foodRecyclerView.adapter = adapter
                    } else {
                        binding.noProductsTextView.visibility = View.VISIBLE
                        binding.etSearchFood.visibility = View.GONE
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

    private fun filterProducts(query: String): List<Product> {
        // Filter the shops based on the search query
        return products?.filter { product ->
            product.productName.contains(query, ignoreCase = true)
        } ?: emptyList()
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
            putString("ShopId", shopId)
            putString("ProductId", product.productId)
            putString("ProductName", product.productName)
        }

        findNavController().navigate(R.id.action_foodListFragment_to_foodDetailFragment, bundle)
    }
}