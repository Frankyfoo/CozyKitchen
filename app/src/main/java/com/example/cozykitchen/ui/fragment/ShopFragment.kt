package com.example.cozykitchen.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
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

    private var userLocation: LatLng? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Request location permission
        // ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // get user last known location
                        userLocation = LatLng(location.latitude, location.longitude)
//                        Log.d("LocationTesting", "${userLocation!!.latitude}, ${userLocation!!.longitude}")
                    }
                }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }

        binding = FragmentShopBinding.inflate(inflater, container, false)
        shopRecyclerView = binding.shopRecyclerView
        shopRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Api Call and RecyclerView Populated
        KitchenApi.retrofitService.getShops().enqueue(object : Callback<List<Shop>?> {
            override fun onResponse(call: Call<List<Shop>?>, response: Response<List<Shop>?>) {
                val shops = response.body() as MutableList?

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
        return binding.root
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