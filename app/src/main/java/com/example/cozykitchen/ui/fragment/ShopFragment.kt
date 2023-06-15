package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.addCallback
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_shop, container, false)
        binding = FragmentShopBinding.inflate(inflater, container, false)
        shopRecyclerView = binding.shopRecyclerView
        shopRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Api Call and RecyclerView Populated
        KitchenApi.retrofitService.getShops().enqueue(object : Callback<List<Shop>?> {
            override fun onResponse(call: Call<List<Shop>?>, response: Response<List<Shop>?>) {
                val shops = response.body()

                adapter = shops?.let { ShopAdapter(it, this@ShopFragment) }!!
                shopRecyclerView.adapter = adapter
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


}