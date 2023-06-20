package com.example.cozykitchen.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.AddressAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentAddressListBinding
import com.example.cozykitchen.databinding.FragmentProfileBinding
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnAddressClickListener {
    fun onCardClick(address: Address)
    fun onDeleteClick(addressId: String)
}

class AddressListFragment : Fragment(), OnAddressClickListener {

    private lateinit var binding: FragmentAddressListBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: AddressAdapter

    private var addressList: MutableList<Address>? = null

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
        (activity as AppCompatActivity).supportActionBar?.title = "Address List"

        binding = FragmentAddressListBinding.inflate(inflater, container, false)
        val addressRecyclerView: RecyclerView = binding.addressRecyclerView
        addressRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getAddressesByUserId(userId).enqueue(object: Callback<List<Address>>{
            override fun onResponse(call: Call<List<Address>>, response: Response<List<Address>>) {
                addressList = response.body() as MutableList<Address>?

                if(addressList != null && addressList!!.isNotEmpty()) {
                    adapter = AddressAdapter(addressList!!, this@AddressListFragment)
                    addressRecyclerView.adapter = adapter

                    binding.tvNoAddress.visibility = View.GONE
                } else {
                    binding.tvNoAddress.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<List<Address>>, t: Throwable) {
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

        // Request location permission
        // ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)

        // FAB for adding new Address
        binding.fabAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_addressListFragment_to_addressFragment)
        }
    }

    override fun onCardClick(address: Address) {
//        val bundle = Bundle().apply {
//                putString("AddressId", address.addressId)
//        }
//
//        Toast.makeText(requireContext(), "${address.addressId}", Toast.LENGTH_SHORT).show()
//
//        findNavController().navigate(R.id.action_addressListFragment_to_addressFragment, bundle)
    }

    override fun onDeleteClick(addressId: String) {
        KitchenApi.retrofitService.deleteAddress(addressId).enqueue(object: Callback<Address?>{
            override fun onResponse(call: Call<Address?>, response: Response<Address?>) {

                // Address deleted successfully, remove it from the list
                val deletedAddress = findDeletedAddressFromList(addressId)

                deletedAddress?.let {
                    // Remove the deleted address from the list
                    addressList!!.remove(deletedAddress)
                    // Notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Address Deleted Successfully", Toast.LENGTH_SHORT).show()

                    // show no address found text
                    if(addressList.isNullOrEmpty()) {
                        binding.tvNoAddress.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<Address?>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun findDeletedAddressFromList(addressId: String): Address? {
        // Iterate through the addressList and find the deleted address using its ID
        for (address in addressList!!) {
            if (address.addressId == addressId) {
                return address
            }
        }
        return null
    }
}