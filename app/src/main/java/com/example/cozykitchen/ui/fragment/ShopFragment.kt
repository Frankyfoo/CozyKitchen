package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.ShopAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentShopBinding
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.sharedPreference.LoginPreference
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnItemClickListener {
    fun onItemClick(id: String)
}

class ShopFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentShopBinding
    private lateinit var navController: NavController
    private lateinit var session: LoginPreference
    private lateinit var adapter: ShopAdapter
    private lateinit var shopRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.back_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
////                 Handle back button click
//                navController = NavHostFragment.findNavController(this)
//                navController.navigateUp()
//                true
////                requireActivity().onBackPressed()
////                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_shop, container, false)
        binding = FragmentShopBinding.inflate(inflater, container, false)
        shopRecyclerView = binding.shopRecyclerView
        shopRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        session = LoginPreference(requireContext())

//        val bundle = Bundle()
//        bundle.putString("CustomerId", "${session.getUserDetails()["userId"]}")
//        bundle.putParcelable("User", session.convertToUserObject())
//        Log.d("ShopTesting", "${session.convertToUserObject()}")

        // Obtain reference to NavController
//        navController = Navigation.findNavController(view)

//        binding.buttonTesting.setOnClickListener {
//            navController.navigate(R.id.action_shop_fragment_to_cart_fragment, bundle)
//        }
    }

    override fun onItemClick(id: String) {
        // Handle item click event here
        Toast.makeText(requireContext(), "Item clicked at position: $id", Toast.LENGTH_SHORT).show()

        

        // Obtain reference to NavController
        navController = view?.let { Navigation.findNavController(it) }!!
        navController.navigate(R.id.action_shop_fragment_to_foodListFragment)

    }


}