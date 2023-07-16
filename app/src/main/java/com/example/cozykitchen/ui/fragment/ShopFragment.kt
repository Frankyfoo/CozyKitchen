package com.example.cozykitchen.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.ShopAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentShopBinding
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnShopClickListener {
    fun onItemClick(shop: Shop)
}

class ShopFragment : Fragment(), OnShopClickListener {

    private lateinit var binding: FragmentShopBinding
    private lateinit var adapter: ShopAdapter
    private lateinit var shopRecyclerView: RecyclerView
    private lateinit var etSearchShop: EditText

    private var userLocation: LatLng? = null

    private var searchQuery: String = ""
    private var shops: MutableList<Shop>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // get user last known location
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            loadShopList()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }

        binding = FragmentShopBinding.inflate(inflater, container, false)
        shopRecyclerView = binding.shopRecyclerView
        shopRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        etSearchShop = binding.etSearchShop
        etSearchShop.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the search query
                searchQuery = s.toString().trim()

                // Filter the shop list based on the search query
                val filteredShops = filterShops(searchQuery)

                // Update the adapter with the filtered shops
                adapter.updateShopList(filteredShops, searchQuery)

                // Show/hide the "No Shops" text based on the filtered shops
                binding.noShopsTextView.visibility = if (filteredShops.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })

        return binding.root
    }

    private fun loadShopList() {
        // Api Call and RecyclerView Populated
        KitchenApi.retrofitService.getShops().enqueue(object : Callback<List<Shop>?> {
            override fun onResponse(call: Call<List<Shop>?>, response: Response<List<Shop>?>) {
                shops = response.body()?.toMutableList()

                // calculate and store the distance value between user and each shop
                shops?.forEach { shop ->
                    userLocation?.let {
                        val distance = calculateDistance(it, LatLng(shop.latitude, shop.longitude))
                        shop.distance = distance / 1000f
                    }
                }

                // sort the Shops List by distance
                shops?.sortBy { it.distance }

                requireActivity().runOnUiThread {
                    adapter = shops?.let { ShopAdapter(it, this@ShopFragment) }!!
                    shopRecyclerView.adapter = adapter
                }

                // Todo: Code below show all shops sort to nearest by 5km
                // Filter the shops based on the distance less than 5 km
//                val filteredShops = shops?.filter { it.distance!! < 5f }

                // Sort the filtered shops list by distance
//                val sortedShops = filteredShops?.sortedBy { it.distance }

                // only run the adapter after the distance has been calculated
//                requireActivity().runOnUiThread {
//                    // Create a new adapter with the updated shops list
//                    val updatedAdapter = sortedShops?.let { ShopAdapter(it, this@ShopFragment) }
//                    shopRecyclerView.adapter = updatedAdapter
//
//                    // Notify the adapter that the data set has changed
//                    updatedAdapter?.notifyDataSetChanged()
//                }
            }

            override fun onFailure(call: Call<List<Shop>?>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterShops(query: String): List<Shop> {
        // Filter the shops based on the search query
        return shops?.filter { shop ->
            shop.shopName.contains(query, ignoreCase = true)
        } ?: emptyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }
    }

    override fun onItemClick(shop: Shop) {
        val bundle = Bundle()
        bundle.putString("ShopId", shop.shopId)
        bundle.putString("ShopName", shop.shopName)

        findNavController().navigate(R.id.action_shop_fragment_to_foodListFragment, bundle)
    }

    private fun calculateDistance(userLocation: LatLng, shopLocation: LatLng): Float {
        val userLocationPoint = Location("")
        userLocationPoint.latitude = userLocation.latitude
        userLocationPoint.longitude = userLocation.longitude

        val shopLocationPoint = Location("")
        shopLocationPoint.latitude = shopLocation.latitude
        shopLocationPoint.longitude = shopLocation.longitude

        return userLocationPoint.distanceTo(shopLocationPoint)
    }


}