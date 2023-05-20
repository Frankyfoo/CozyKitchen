package com.example.cozykitchen.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.ActivityMainBinding
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.fragment.ShopFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Appbar Title set to shop as the first loaded screen is shop
        supportActionBar?.title = "Shop"

        // start of setting up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavigationView = binding.bottomNavBar
        navController = navHostFragment.navController
//        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        // end

        binding.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_shop -> {
                    supportActionBar?.title = "Shop"
                    navController.navigate(R.id.shop_fragment)
                    true
                }
                R.id.navigation_cart -> {
                    supportActionBar?.title = "Cart"
                    navController.navigate(R.id.cart_fragment)
                    true
                }
                R.id.navigation_history -> {
                    supportActionBar?.title = "History"
                    navController.navigate(R.id.history_fragment)
                    true
                }
                R.id.navigation_profile -> {
                    supportActionBar?.title = "Profile"
                    navController.navigate(R.id.profile_fragment)
                    true
                }
                else -> false
            }
        }
    }
}