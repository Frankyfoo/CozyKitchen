package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.FragmentCartBinding
import com.example.cozykitchen.databinding.FragmentShopBinding
import com.example.cozykitchen.model.User

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_cart, container, false)
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val data = arguments?.getString("CustomerId")
//        Log.d("CartTesting", "$data")
//
//        if (data == null) {
//            binding.tvCart.text = "Cart"
//        } else {
//            binding.tvCart.text = data
//        }
    }
}