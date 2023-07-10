package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.OrderAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentHistoryBinding
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

interface OnOrderClickListener {
    fun onCardClick(order: Order)
}

//class HistoryFragment : Fragment(), OnOrderClickListener {
//
//    private lateinit var binding: FragmentHistoryBinding
//    private lateinit var session: LoginPreference
//    private lateinit var adapter: OrderAdapter
//    private lateinit var spinnerStatus: Spinner
//    private lateinit var orderRecyclerView: RecyclerView
//
//    private var orderList: MutableList<Order> = mutableListOf()
//    private val statusOptions = listOf("ALL", "PAY_COMPLETED", "DELIVERED", "CANCELLED")
//    private var selectedStatus: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        // Set the title in the app bar
//        (activity as AppCompatActivity).supportActionBar?.title = "History"
//
//        // get current logged in user ID
//        session = LoginPreference(requireContext())
//        val userId = session.getCurrentUserId()
//
//        binding = FragmentHistoryBinding.inflate(inflater, container, false)
//        spinnerStatus = binding.spinnerStatus
//
//        // Set up the spinner adapter
//        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerStatus.adapter = spinnerAdapter
//
//        orderRecyclerView = binding.rvOrderHistory
//        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Initialize the adapter with an empty list
//        adapter = OrderAdapter(emptyList(), this@HistoryFragment)
//        orderRecyclerView.adapter = adapter
//
////        // Initialize the adapter with the orderList
////        adapter = OrderAdapter(orderList, this@HistoryFragment)
////        orderRecyclerView.adapter = adapter
//
//        KitchenApi.retrofitService.getOrdersByUserId(userId).enqueue(object : Callback<List<Order>?>{
//            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
//                if (response.isSuccessful) {
//                    orderList = response.body() as MutableList<Order>
//                    // to start the order in reverse
//                    orderList.reverse()
//
//                    // This will be run when the screen is first run.
//                    if (!orderList.isNullOrEmpty()){
//                        adapter = OrderAdapter(orderList, this@HistoryFragment)
//                        orderRecyclerView.adapter = adapter
//
//                        binding.tvNoHistory.visibility = View.GONE
//                    } else {
//                        binding.tvNoHistory.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
//                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        // Inflate the layout for this fragment
//        return binding.root
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Call the function to filter the orders based on the selected status
////        filterOrders(selectedStatus)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Set up the spinner listener
//        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                selectedStatus = statusOptions[position]
//                // Call a function to filter the orders based on the selected status
//                filterOrders(selectedStatus)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Handle the case when nothing is selected
//                selectedStatus = null
//                // Call a function to filter the orders based on the selected status
//                filterOrders(selectedStatus)
//            }
//        }
//
////        // Initialize the adapter with the orderList
////        adapter = OrderAdapter(orderList, this@HistoryFragment)
////        orderRecyclerView.adapter = adapter
//
//        // Close the application when back button is pressed
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            requireActivity().finishAffinity()
//        }
//
//        // Hide the back button in the app bar
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
//    }
//
//    private fun filterOrders(status: String?) {
//        val filteredList = if (status.isNullOrEmpty() || status == "ALL") {
//            // Show all orders
//            orderList
//        } else {
//            // Filter orders based on status
//            orderList.filter { it.status == status }
//        }
//
//        // Update the adapter with the filtered list
//        adapter.updateOrderList(filteredList)
//
//        // Show/hide the "No History" text based on the filtered list
//        binding.tvNoHistory.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
//    }
//
//    override fun onCardClick(order: Order) {
////        Toast.makeText(requireContext(), "$orderId", Toast.LENGTH_SHORT).show()
//        val bundle = Bundle()
//        bundle.apply {
//            putString("OrderId", order.orderId)
//            putString("DeliveryDateTimeString", order.deliveryDateTimeString)
//            putString("OrderStatus", order.status)
//        }
//
//        findNavController().navigate(R.id.action_history_fragment_to_orderedFoodList, bundle)
//    }
//}

class HistoryFragment : Fragment(), OnOrderClickListener {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: OrderAdapter
    private lateinit var spinnerStatus: Spinner
    private lateinit var orderRecyclerView: RecyclerView

    private var orderList: MutableList<Order> = mutableListOf()
    private val statusOptions = listOf("ALL", "PAY_COMPLETED", "DELIVERED", "CANCELLED")
    private var selectedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "History"

        // get current logged in user ID
        session = LoginPreference(requireContext())
        val userId = session.getCurrentUserId()

        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        spinnerStatus = binding.spinnerStatus

        // Set up the spinner adapter
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = spinnerAdapter

        orderRecyclerView = binding.rvOrderHistory
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list
        adapter = OrderAdapter(emptyList(), this@HistoryFragment)
        orderRecyclerView.adapter = adapter

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the spinner listener
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatus = statusOptions[position]
                // Call a function to filter the orders based on the selected status
                filterOrders(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected
                selectedStatus = null
                // Call a function to filter the orders based on the selected status
                filterOrders(selectedStatus)
            }
        }

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        // Hide the back button in the app bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // get current logged in user ID
        session = LoginPreference(requireContext())
        val userId = session.getCurrentUserId()

        KitchenApi.retrofitService.getOrdersByUserId(userId).enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                if (response.isSuccessful) {
                    orderList = response.body() as MutableList<Order>


                    // to start the order in reverse
                    orderList.reverse()

                    // This will be run when the screen is first run.
                    if (!orderList.isNullOrEmpty()) {
                        // Update the adapter with the fetched order list
                        adapter.updateOrderList(orderList)
                        binding.tvNoHistory.visibility = View.GONE
                    } else {
                        binding.tvNoHistory.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Call the function to filter the orders based on the selected status
        filterOrders(selectedStatus)
    }

    private fun filterOrders(status: String?) {
        val filteredList = if (status.isNullOrEmpty() || status == "ALL") {
            // Show all orders
            orderList
        } else {
            // Filter orders based on status
            orderList.filter { it.status == status }
        }

        // Update the adapter with the filtered list
        adapter.updateOrderList(filteredList)

        // Show/hide the "No History" text based on the filtered list
        binding.tvNoHistory.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCardClick(order: Order) {
        val bundle = Bundle().apply {
            putString("OrderId", order.orderId)
            putString("DeliveryDateTimeString", order.deliveryDateTimeString)
            putString("OrderStatus", order.status)
            putString("ShopId", order.shopId)
        }

        findNavController().navigate(R.id.action_history_fragment_to_orderedFoodList, bundle)
    }
}