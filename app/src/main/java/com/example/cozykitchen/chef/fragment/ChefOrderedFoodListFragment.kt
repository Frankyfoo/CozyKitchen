package com.example.cozykitchen.chef.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.P
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
import androidx.core.view.marginLeft
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.CartAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentChefOrderedFoodListBinding
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChefOrderedFoodListFragment : Fragment() {

    private lateinit var binding: FragmentChefOrderedFoodListBinding
    private lateinit var adapter: CartAdapter
    private lateinit var btnUpdateOrder: Button
    private lateinit var btnDeleteOrder: Button
    private lateinit var btnCustomerInformation: Button

    private var shoppingCartList: MutableList<ShoppingCart> = mutableListOf()
    private var user: User? = null
    private var address: Address? = null
    private var orderId: String? = null

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
        val userId = arguments?.getString("UserId")
        val addressId = arguments?.getString("AddressId")

        binding = FragmentChefOrderedFoodListBinding.inflate(inflater, container, false)
        btnUpdateOrder = binding.btnUpdateOrder
        btnDeleteOrder = binding.btnDeleteOrder
        btnCustomerInformation = binding.btnCustomerInformation

        val orderedRecyclerView: RecyclerView = binding. rvOrderedFood
        orderedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // get shopping cart list
        KitchenApi.retrofitService.getShoppingCartListByOrderId(orderId!!).enqueue(object:
            Callback<List<ShoppingCart>> {
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
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }

        })

        // get user information
        KitchenApi.retrofitService.getUserById(userId!!).enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        // get address information
        KitchenApi.retrofitService.getAddressById(addressId!!).enqueue(object: Callback<Address>{
            override fun onResponse(call: Call<Address>, response: Response<Address>) {
                if (response.isSuccessful) {
                    address = response.body()
                }
            }

            override fun onFailure(call: Call<Address>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
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

        val status = arguments?.getString("Status")

        if (status == "CANCELLED" || status == "DELIVERED") {
            btnUpdateOrder.visibility = View.GONE
            btnDeleteOrder.visibility = View.GONE
        } else {
            btnUpdateOrder.visibility = View.VISIBLE
            btnDeleteOrder.visibility = View.VISIBLE
        }

        btnUpdateOrder.setOnClickListener {
            // Create an AlertDialog.Builder object
            val builder = AlertDialog.Builder(requireContext())

            // Set the title and message of the dialog
            builder.setTitle("Update Order")
            builder.setMessage("Do you want to update this order as delivered?")

            // Set a positive button and its click listener
            builder.setPositiveButton("Yes") { dialog, which ->
                KitchenApi.retrofitService.updateOrderByOrderId(orderId!!).enqueue(object: Callback<Order>{
                    override fun onResponse(call: Call<Order>, response: Response<Order>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Order updated successfully.", Toast.LENGTH_SHORT).show()

                            // return to previous page
                            findNavController().navigateUp()
                        }
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {
                        Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }

            builder.setNegativeButton("No") {dialog, which ->
                // Handle the button click
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

        btnDeleteOrder.setOnClickListener {
            // Create an AlertDialog.Builder object
            val builder = AlertDialog.Builder(requireContext())

            // Set the title and message of the dialog
            builder.setTitle("Delete Order")
            builder.setMessage("Do you want to cancel this order?")

            // Set a positive button and its click listener
            builder.setPositiveButton("Yes") { dialog, which ->
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

            builder.setNegativeButton("No") {dialog, which ->
                // Handle the button click
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

        btnCustomerInformation.setOnClickListener {
            // Create an AlertDialog.Builder object
            val builder = AlertDialog.Builder(requireContext())

            // Set the title and message of the dialog
            builder.setTitle("Customer Information")
            builder.setMessage("Name: ${user?.userName}\n\nPhone Number: ${user?.userPhoneNumber}\n" +
                    "\nStreet: ${address?.street}\n\nStreet Number: ${address?.streetNumber}\n\n" +
                    "Instruction: ${address?.instruction}")

            // Inflate the custom layout
            val dialogView = layoutInflater.inflate(R.layout.dialog_customer_information, null)
            val btnCall = dialogView.findViewById<Button>(R.id.btn_call)
            val btnNavigate = dialogView.findViewById<Button>(R.id.btn_navigate)

            btnCall.setOnClickListener {
                // Perform any desired action when btnCall is clicked
                // For example, you can initiate a phone call
                val phoneNumber = user?.userPhoneNumber
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(intent)
            }

            btnNavigate.setOnClickListener {
                // Perform any desired action when btnNavigate is clicked
                // For example, you can open a navigation app
                val latitude = address?.latitude
                val longitude = address?.longitude
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$latitude,$longitude"))
                startActivity(intent)
            }

            // Set the custom view of the dialog
            builder.setView(dialogView)

            // Set a positive button and its click listener
            builder.setPositiveButton("OK") { dialog, which ->
                // Handle the button click
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}