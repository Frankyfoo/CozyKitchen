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
import com.example.cozykitchen.adapter.OrderAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentHistoryBinding
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnOrderClickListener {
    fun onCardClick(orderId: String)
}

class HistoryFragment : Fragment(), OnOrderClickListener {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: OrderAdapter

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
        val orderRecyclerView: RecyclerView = binding.rvOrderHistory
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getOrdersByUserId(userId).enqueue(object : Callback<List<Order>?>{
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()){
                        adapter = OrderAdapter(body, this@HistoryFragment)
                        orderRecyclerView.adapter = adapter

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
    }

    override fun onCardClick(orderId: String) {
//        Toast.makeText(requireContext(), "$orderId", Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.apply {
            putString("OrderId", orderId)
        }

        findNavController().navigate(R.id.action_history_fragment_to_orderedFoodList, bundle)
    }
}