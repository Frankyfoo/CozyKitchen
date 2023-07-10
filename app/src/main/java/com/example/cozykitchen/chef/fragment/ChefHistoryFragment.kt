package com.example.cozykitchen.chef.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.OrderAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentChefHistoryBinding
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.fragment.OnOrderClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChefHistoryFragment : Fragment(), OnOrderClickListener {

    private lateinit var binding: FragmentChefHistoryBinding
    private lateinit var session: LoginPreference
    private lateinit var btnTimeRange: Button
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter

    private var shopId: String? = null
    private var orderList: MutableList<Order>? = null
    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "History"

        binding = FragmentChefHistoryBinding.inflate(layoutInflater, container, false)
        btnTimeRange = binding.btnTimeRange
        orderRecyclerView = binding.rvOrderHistory
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // get chef Id
        session = LoginPreference(requireContext())
        val chefId = session.getCurrentUserId()

        // get ShopId based on chef Id
        KitchenApi.retrofitService.getChefById(chefId).enqueue(object: Callback<Chef>{
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if (response.isSuccessful) {
                    var chef = response.body()
                    if (chef != null) {
                        shopId = chef.shopId
                        if (!shopId.isNullOrEmpty()) {
                            KitchenApi.retrofitService.getOrdersByShopId(shopId!!).enqueue(object: Callback<List<Order>>{
                                override fun onResponse(
                                    call: Call<List<Order>>,
                                    response: Response<List<Order>>
                                ) {
                                    if (response.isSuccessful) {
                                        orderList = response.body() as MutableList<Order>
                                        if (!orderList.isNullOrEmpty()) {

                                            // add LocalDateTime object to each items in orderList
                                            orderList = orderList!!.map { order ->
                                                order.copy(deliveryLocalDateTime = LocalDateTime.parse(order.deliveryDateTime, formatter))
                                            }.toMutableList()

                                            orderList!!.reverse()

//                                            Log.d("TestingOrder", "$orderList")

                                            adapter = OrderAdapter(orderList!!, this@ChefHistoryFragment)
                                            orderRecyclerView.adapter = adapter

                                            binding.tvNoHistory.visibility = View.GONE
                                        } else {
                                            binding.tvNoHistory.visibility = View.VISIBLE
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            binding.llSpinner.visibility = View.GONE
                            orderRecyclerView.visibility = View.GONE
                        }
//                        shopId?.let {
//                            KitchenApi.retrofitService.getOrdersByShopId(it).enqueue(object: Callback<List<Order>>{
//                                override fun onResponse(
//                                    call: Call<List<Order>>,
//                                    response: Response<List<Order>>
//                                ) {
//                                    if (response.isSuccessful) {
//                                        orderList = response.body() as MutableList<Order>
//                                        if (!orderList.isNullOrEmpty()) {
//
//                                            // add LocalDateTime object to each items in orderList
//                                            orderList = orderList!!.map { order ->
//                                                order.copy(deliveryLocalDateTime = LocalDateTime.parse(order.deliveryDateTime, formatter))
//                                            }.toMutableList()
//
//                                            orderList!!.reverse()
//
////                                            Log.d("TestingOrder", "$orderList")
//
//                                            adapter = OrderAdapter(orderList!!, this@ChefHistoryFragment)
//                                            orderRecyclerView.adapter = adapter
//
//                                            binding.tvNoHistory.visibility = View.GONE
//                                        } else {
//                                            binding.tvNoHistory.visibility = View.VISIBLE
//                                        }
//                                    }
//                                }
//
//                                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
//                                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
//                                }
//                            })
//                        }
                    }
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTimeRange.setOnClickListener {
            showAlertDialog()
        }

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        // Hide the back button in the app bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun showAlertDialog() {
        // Create an AlertDialog.Builder object
        val builder = AlertDialog.Builder(requireContext())

        // Set the title and message of the dialog
        builder.setTitle("Select start and end date:")

        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)
        val startDatePicker = dialogView.findViewById<DatePicker>(R.id.startDatePicker)
        val endDatePicker = dialogView.findViewById<DatePicker>(R.id.endDatePicker)

        // Set the custom view of the dialog
        builder.setView(dialogView)

        // Set a positive button and its click listener
        builder.setPositiveButton("Select") { dialog, which ->
            // Retrieve the selected start and end dates from the DatePickers
            val startYear = startDatePicker.year
            val startMonth = startDatePicker.month
            val startDay = startDatePicker.dayOfMonth

            val endYear = endDatePicker.year
            val endMonth = endDatePicker.month
            val endDay = endDatePicker.dayOfMonth

            // Create LocalDateTime objects from the selected dates
            val startDate = LocalDateTime.of(startYear, startMonth + 1, startDay, 0, 0)
            val endDate = LocalDateTime.of(endYear, endMonth + 1, endDay, 23, 59, 59)

            // Check if the start date is higher than the end date
            if (startDate.isAfter(endDate)) {
                // Show an error message or perform any desired error handling
                Toast.makeText(requireContext(), "Invalid date range", Toast.LENGTH_SHORT).show()
            } else {
                // Perform any desired action with the selected date range
                val friendlyDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                btnTimeRange.text = "${startDate.format(friendlyDateFormatter)} - ${endDate.format(friendlyDateFormatter)}"
                filterOrders(startDate, endDate)
            }
        }

        // Set a negative button and its click listener
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Handle the negative button click
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun filterOrders(startDate: LocalDateTime, endDate: LocalDateTime) {
        val filteredList = orderList?.filter { order ->
            val orderDateTime = order.deliveryLocalDateTime
            orderDateTime.isAfter(startDate) && orderDateTime.isBefore(endDate)
        }

        if (!filteredList.isNullOrEmpty()) {
//            adapter.clearOrderList() // Clear the existing order list
            adapter.updateOrderList(filteredList)
            binding.tvNoHistory.visibility = View.GONE
        } else {
            adapter.updateOrderList(emptyList())
            binding.tvNoHistory.visibility = View.VISIBLE
        }
    }

    override fun onCardClick(order: Order) {
        val bundle = Bundle()
        bundle.apply {
            putString("OrderId", order.orderId)
            putString("UserId", order.userId)
            putString("AddressId", order.addressId)
            putString("Status", order.status)
        }

        findNavController().navigate(R.id.action_chefHistoryFragment_to_chefOrderedFoodListFragment, bundle)
    }
}