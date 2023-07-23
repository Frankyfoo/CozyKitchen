package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.CartAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentCartBinding
import com.example.cozykitchen.databinding.FragmentShopBinding
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.model.User
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnCartClickListener {
    fun onRemoveButtonClick(shoppingCartId: String)
}

class CartFragment : Fragment(), OnCartClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var session: LoginPreference
    private lateinit var btnPurchase: Button

    private var totalCost: Float = 0.0f
    private var deliveryCost: Float = 3.0f
    private var shoppingCartList: MutableList<ShoppingCart>? = null
    private var shopId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get current logged in user ID
        session = LoginPreference(requireContext())
        val userId = session.getCurrentUserId()

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Cart"

        binding = FragmentCartBinding.inflate(inflater, container, false)

        // bind to variable
        btnPurchase = binding.btnPurchase

        // This is set so that the bottomLayout will not show when the cart is empty
        binding.linearLayoutBottom.visibility = View.GONE

        val cartRecyclerView: RecyclerView = binding.cartRecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getShoppingCartListByUserId(userId).enqueue(object: Callback<List<ShoppingCart>>{
                override fun onResponse(
                    call: Call<List<ShoppingCart>>,
                    response: Response<List<ShoppingCart>>
                ) {
                    shoppingCartList = response.body() as MutableList<ShoppingCart>?

                    if (shoppingCartList != null && shoppingCartList!!.isNotEmpty()) {
                        for(shoppingCart in shoppingCartList!!) {
                            shopId = shoppingCart.product?.shopId
                        }

                        adapter = CartAdapter(shoppingCartList!!, this@CartFragment)
                        cartRecyclerView.adapter = adapter

                        // show bottom layout (Price) and hide no cart found text
                        binding.tvNoCartItemFound.visibility = View.GONE
                        binding.linearLayoutBottom.visibility = View.VISIBLE

                        for (shoppingCart in shoppingCartList!!) {
                            if (shoppingCart.size == "Large") {
                                val price = (shoppingCart.product!!.productPrice + 2.00f) * shoppingCart.quantityBought
                                totalCost += price
                            } else {
                                val price = shoppingCart.product!!.productPrice * shoppingCart.quantityBought
                                totalCost += price
                            }
                        }

                        // cost after adding delivery
                        var finalTotalCost = totalCost + deliveryCost

                        //show costs
                        binding.tvCost.text = "Cost: RM ${String.format("%.2f", totalCost)}"
                        binding.tvDeliveryCost.text = "Delivery Cost: RM ${String.format("%.2f", deliveryCost)}"
                        binding.tvTotalCost.text = "Total Cost: RM ${String.format("%.2f", finalTotalCost)}"
                    } else {
                        // disable bottom layout (Price) and show no cart found text
                        binding.tvNoCartItemFound.visibility = View.VISIBLE
                        binding.linearLayoutBottom.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<List<ShoppingCart>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

            })

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

        btnPurchase.setOnClickListener {
            val bundle = Bundle()
            bundle.putFloat("TotalCost", String.format("%.2f", totalCost).toFloat())
            bundle.putString("UserId", session.getCurrentUserId())
            bundle.putString("ShopId", shopId)

            findNavController().navigate(R.id.action_cart_fragment_to_confirmPurchaseFragment, bundle)
        }
    }

    override fun onRemoveButtonClick(shoppingCartId: String) {
        KitchenApi.retrofitService.deleteShoppingCart(shoppingCartId).enqueue(object: Callback<ShoppingCart>{
            override fun onResponse(call: Call<ShoppingCart>, response: Response<ShoppingCart>) {

                // ShoppingCart deleted successfully, remove it from the list
                val deletedShoppingCart = findDeletedShoppingCartFromList(shoppingCartId)

                deletedShoppingCart.let {
                    // Remove the deleted ShoppingCart Item from the list
                    shoppingCartList!!.remove(deletedShoppingCart)
                    // Notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show()

                    // Recalculate the total cost
                    calculateTotalCost()

                    // Show/hide the appropriate views based on the shopping cart list
                    handleEmptyCart()

                    // Update the price and total cost
                    updatePriceAndTotalCost()

                    // show no shopping cart item found text
                    if (shoppingCartList.isNullOrEmpty()) {
                        binding.tvNoCartItemFound.visibility = View.VISIBLE
                        binding.linearLayoutBottom.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ShoppingCart>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun findDeletedShoppingCartFromList(shoppingCartId: String): ShoppingCart? {
        // Iterate through the shoppingCartList and find the deleted address using its ID
        for (shoppingCart in shoppingCartList!!) {
            if (shoppingCart.shoppingCartId == shoppingCartId) {
                return shoppingCart
            }
        }
        return null
    }

    private fun calculateTotalCost() {
        totalCost = 0.0f

        for (shoppingCart in shoppingCartList!!) {
            if (shoppingCart.size == "Large") {
                val price = (shoppingCart.product!!.productPrice + 2.00f) * shoppingCart.quantityBought
                totalCost += price
            } else {
                val price = shoppingCart.product!!.productPrice * shoppingCart.quantityBought
                totalCost += price
            }
        }
    }

    private fun handleEmptyCart() {
        if (shoppingCartList.isNullOrEmpty()) {
            binding.tvNoCartItemFound.visibility = View.VISIBLE
            binding.linearLayoutBottom.visibility = View.GONE
        }
    }

    private fun updatePriceAndTotalCost() {
        // Calculate the final total cost (including delivery cost)
        val finalTotalCost = totalCost + deliveryCost

        // Update the price and total cost TextViews
        binding.tvCost.text = "Cost: RM ${String.format("%.2f", totalCost)}"
        binding.tvTotalCost.text = "Total Cost: RM ${String.format("%.2f", finalTotalCost)}"
    }
}