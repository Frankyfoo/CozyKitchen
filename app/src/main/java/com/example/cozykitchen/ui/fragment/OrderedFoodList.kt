package com.example.cozykitchen.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.adapter.CartAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentOrderedFoodListBinding
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.model.ShoppingCart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OrderedFoodList : Fragment() {

    private lateinit var binding: FragmentOrderedFoodListBinding
    private lateinit var adapter: CartAdapter
    private lateinit var btnDeleteOrder: Button
    private lateinit var btnShopInfo: Button

    private var currentTime : Date? = null
    private var deliveryDateTime: Date? = null
    private var orderId: String? = null
    private var status: String? = null
    private var shoppingCartList: MutableList<ShoppingCart> = mutableListOf()

    private var shopId: String? = null
    private var chef: Chef? = null
    private var shop: Shop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Ordered Food"

        // get data from previous screen
        orderId = arguments?.getString("OrderId")
        status = arguments?.getString("OrderStatus")
        shopId = arguments?.getString("ShopId")
        val deliveryDateTimeString = arguments?.getString("DeliveryDateTimeString")

        // convert DeliveryDateTimeString to Date object
        deliveryDateTime = convertToDate(deliveryDateTimeString)

        // get current Date object starting from 12:00 AM
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        currentTime = calendar.time

        binding = FragmentOrderedFoodListBinding.inflate(inflater, container, false)
        btnDeleteOrder = binding.btnDeleteOrder
        btnShopInfo = binding.btnShopInfo

        val orderedRecyclerView: RecyclerView = binding.rvOrderedFood
        orderedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // get shop info
        KitchenApi.retrofitService.getShopById(shopId!!).enqueue(object: Callback<Shop>{
            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                if(response.isSuccessful) {
                    shop = response.body()
                }
            }

            override fun onFailure(call: Call<Shop>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        // get chef info
        KitchenApi.retrofitService.getChefByShopId(shopId!!).enqueue(object: Callback<Chef>{
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if(response.isSuccessful) {
                    chef = response.body()
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        // get shopping carts item by order Id
        KitchenApi.retrofitService.getShoppingCartListByOrderId(orderId!!).enqueue(object: Callback<List<ShoppingCart>>{
            override fun onResponse(
                call: Call<List<ShoppingCart>>,
                response: Response<List<ShoppingCart>>
            ) {
                if(response.isSuccessful) {
                    shoppingCartList = response.body() as MutableList<ShoppingCart>
                    if(shoppingCartList.isNotEmpty()) {
                        adapter = CartAdapter(shoppingCartList)
                        orderedRecyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<ShoppingCart>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun convertToDate(deliveryDateTimeString: String?): Date? {
        return if (!deliveryDateTimeString.isNullOrEmpty()) {
            // create formatter
            val formatter = SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault())
            // Extract the time string inside parentheses
            val extractedTimeString = deliveryDateTimeString!!.substring(deliveryDateTimeString!!.indexOf('(') + 1, deliveryDateTimeString!!.indexOf(')'))
            // convert to LocalDateTime object
            val dateTime = formatter.parse(extractedTimeString)
            dateTime
        } else {
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if the order is to be delivered within 24 hours to not
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        val isWithin24Hours = currentTime!!.after(Date(deliveryDateTime!!.time - twentyFourHoursInMillis))

        if (isWithin24Hours || status == "DELIVERED" || status == "CANCELLED") {
            // Delivery is within 24 hours, hide the delete button
            btnDeleteOrder.visibility = View.GONE
        } else {
            // Delivery is more than 24 hours, show the delete button
            btnDeleteOrder.visibility = View.VISIBLE
        }

        btnShopInfo.setOnClickListener {
            showShopInfoAlertDialog()
        }

        // Deletes Order
        btnDeleteOrder.setOnClickListener {
            showCancelAlertDialog()
        }

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun showShopInfoAlertDialog() {
        // Create an AlertDialog.Builder object
        val builder = AlertDialog.Builder(requireContext())

        // Set the title and message of the dialog
        builder.setTitle("Shop Information")
            .setMessage("Shop name: ${shop?.shopName}\n\nChef name: ${chef?.chefName}\n\nPhone Number: " +
                    "${chef?.chefPhoneNumber}")

        builder.setPositiveButton("Ok") {dialog, which ->
            dialog.dismiss()
        }

        builder.setNegativeButton("Call chef") {dialog, which ->
            val phoneNumber = chef?.chefPhoneNumber
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showCancelAlertDialog() {
        // Create an AlertDialog.Builder object
        val builder = AlertDialog.Builder(requireContext())

        // Set the title and message of the dialog
        builder.setTitle("Cancel Order")
            .setMessage("Do you want to cancel the order?")

        // Set a positive button and its click listener
        builder.setPositiveButton("Yes") { dialog, which ->
            // Handle the positive button click
            // You can perform any desired action here
            KitchenApi.retrofitService.deleteOrderByOrderId(orderId!!).enqueue(object: Callback<Order>{
                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Order deleted successfully.", Toast.LENGTH_SHORT).show()

                        // return to previous page
                        findNavController().navigateUp()
                    }
                }

                override fun onFailure(call: Call<Order>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("Testing", "${t.message}")
                }

            })
        }

        // Set a negative button and its click listener
        builder.setNegativeButton("No") { dialog, which ->
            // Handle the negative button click
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }
}