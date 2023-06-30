package com.example.cozykitchen.chef.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.ActivityChefMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChefMainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityChefMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChefMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // start of setting up navigation
        bottomNavigationView = binding.chefBottomNavBar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.chef_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_shop -> {
                    navController.navigate(R.id.menuFragment)
                    true
                }
                R.id.navigation_history -> {
                    navController.navigate(R.id.chefHistoryFragment)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.chefProfileFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}