package com.example.cozykitchen.chef.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.ActivityChefMainBinding
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChefMainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityChefMainBinding
    private lateinit var session: LoginPreference

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
                R.id.navigation_summary -> {
                    navController.navigate(R.id.chefSummaryFragment)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.chefProfileFragment)
                    true
                }

                else -> false
            }
        }

        // set shop id in shared preference when returning to main activity
        session = LoginPreference(this)
        val chefId = session.getCurrentUserId()
        if (session.getShopId() == "") {
            KitchenApi.retrofitService.getChefById(chefId).enqueue(object: Callback<Chef> {
                override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                    if (response.isSuccessful) {
                        val chef = response.body()
                        chef?.shopId?.let {
                            session.createShopIdSession(it)
                        }
                    }
                }

                override fun onFailure(call: Call<Chef>, t: Throwable) {
                    Toast.makeText(this@ChefMainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}