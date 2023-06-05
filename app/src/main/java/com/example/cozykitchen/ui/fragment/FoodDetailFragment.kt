package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentFoodDetailBinding
import com.example.cozykitchen.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentFoodDetailBinding
    private lateinit var imageView: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var radioGroupSize: RadioGroup
//    private lateinit var tvIngredients: TextView
    private lateinit var tvPrice: TextView
    private lateinit var btnPlus: Button
    private lateinit var tvQuantity: TextView
    private lateinit var btnMinus: Button
    private lateinit var btnAddToCart: Button

    private var price: Float = 0f
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // getting data from previous screen
        val productId = arguments?.getString("ProductId")
        val productName = arguments?.getString("ProductName")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = productName

        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        // declare all layout items
        imageView = binding.imageFood
        tvDescription = binding.tvDescription
        radioGroupSize = binding.radioGroupSize
        tvPrice = binding.tvPrice
        btnMinus = binding.btnMinus
        btnPlus = binding.btnPlus
        btnAddToCart = binding.btnAddToCart
        tvQuantity = binding.tvQuantity

        if (productId != null) {
            KitchenApi.retrofitService.getFoodById(productId).enqueue(object: Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {

                    val product = response.body()

                    if (product != null) {
                        if (product.productImageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(R.drawable.no_image)
                                .into(imageView)
                        } else {
                            Glide.with(requireContext())
                                .load(product.productImageUrl)
                                .into(imageView)
                        }

                        tvDescription.text = product.productDescription
                        tvPrice.text = "RM ${product.productPrice}"
                        price = product.productPrice
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroupSize.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.radio_button_normal -> {
                    tvPrice.text = "RM ${price}"
                }
                R.id.radio_button_large -> {
                    tvPrice.text = "RM ${price + 2.00f}"
                }
            }
        }

        btnMinus.setOnClickListener {
            if (quantity <= 1) {
                Toast.makeText(requireContext(), "Quantity must be at least 1", Toast.LENGTH_SHORT).show()
            } else {
                quantity -= 1
                tvQuantity.text = "$quantity"
            }
        }

        btnPlus.setOnClickListener {
            quantity += 1
            tvQuantity.text = "$quantity"
        }



        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }
}