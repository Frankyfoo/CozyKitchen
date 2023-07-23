package com.example.cozykitchen.ui.fragment

import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentFoodDetailBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.TimeListGenerator
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodDetailFragment : Fragment() {

    private lateinit var session: LoginPreference
    private lateinit var binding: FragmentFoodDetailBinding
    private lateinit var imageView: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var radioGroupSize: RadioGroup
    private lateinit var tvIngredients: TextView
    private lateinit var etCustomization: EditText
    private lateinit var tvPrice: TextView
    private lateinit var btnPlus: ImageButton
    private lateinit var tvQuantity: TextView
    private lateinit var btnMinus: ImageButton
    private lateinit var btnAddToCart: Button

    private var price: Float = 0f
    private var quantity: Int = 1
    private var size: String = "Normal"
    private var productId: String = ""
    private var currentUserId: String = ""
    private var customizationText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // getting data from previous screen
        val shopId = arguments?.getString("ShopId")
        val productId = arguments?.getString("ProductId")
        val productName = arguments?.getString("ProductName")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = productName

        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        // declare all layout items
        imageView = binding.imageFood
        tvDescription = binding.tvDescription
        radioGroupSize = binding.radioGroupSize
        tvIngredients = binding.tvIngredients
        tvPrice = binding.tvPrice
        btnMinus = binding.btnMinus
        btnPlus = binding.btnPlus
        btnAddToCart = binding.btnAddToCart
        tvQuantity = binding.tvQuantity
        etCustomization = binding.etCustomization

        if (productId != null) {
            KitchenApi.retrofitService.getFoodById(productId).enqueue(object: Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {

                    val product = response.body()

                    if (product != null) {
                        if (product.productUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(R.drawable.no_image)
                                .into(imageView)
                        } else {
                            Glide.with(requireContext())
                                .load(product.productUrl)
                                .into(imageView)
                        }

                        tvDescription.text = product.productDescription
                        tvIngredients.text = product.productIngredients
                        tvPrice.text = "RM ${product.productPrice}"
                        price = product.productPrice
                        this@FoodDetailFragment.productId = product.productId
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

        // using session to get current logged user id
        session = LoginPreference(requireContext())
        currentUserId = session.getCurrentUserId()

        // select the size of product
        radioGroupSize.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.radio_button_normal -> {
                    tvPrice.text = "RM ${price}"
                    size = "Normal"
                    quantity = 1
                    tvQuantity.text = quantity.toString()
                }
                R.id.radio_button_large -> {
                    tvPrice.text = "RM ${(price + 2.00f)}"
                    size = "Large"
                    quantity = 1
                    tvQuantity.text = quantity.toString()
                }
            }
        }

        // decrease quantity of product
        btnMinus.setOnClickListener {
            if (quantity <= 1) {
                Toast.makeText(requireContext(), "Quantity must be at least 1", Toast.LENGTH_SHORT).show()
            } else {
                quantity -= 1
                tvQuantity.text = "$quantity"
                if (size == "Large") {
                    tvPrice.text = "RM ${((price + 2.00f) * quantity)}"
                } else {
                    tvPrice.text = "RM ${(price * quantity)}"
                }
            }
        }

        // increase quantity of product
        btnPlus.setOnClickListener {
            if (quantity >= 10) {
                Toast.makeText(requireContext(), "Quantity must be less than or equal to 10", Toast.LENGTH_SHORT).show()
            } else {
                quantity += 1
                tvQuantity.text = "$quantity"
                if (size == "Large") {
                    tvPrice.text = "RM ${((price + 2.00f) * quantity)}"
                } else {
                    tvPrice.text = "RM ${(price * quantity)}"
                }
            }
        }

        btnAddToCart.setOnClickListener {
            // get customization text and do some handling
            customizationText = etCustomization.text.toString()

            val cartItem = ShoppingCart(
                "Test", currentUserId, productId, size, quantity, customizationText, "PAY_PENDING", null, null
            )

            val cartRequestBody = RequestBodySingleton.makeGSONRequestBody(cartItem)

            KitchenApi.retrofitService.addProductToCart(cartRequestBody).enqueue(object: Callback<ShoppingCart?> {
                override fun onResponse(
                    call: Call<ShoppingCart?>,
                    response: Response<ShoppingCart?>
                ) {
                    if(response.isSuccessful) {
                        Toast.makeText(requireContext(), "Item Added Successfully", Toast.LENGTH_SHORT).show()

                        // return to foodList Fragment
                        findNavController().popBackStack()
                    } else {
                        showAlertDialog()
                    }
                }

                override fun onFailure(call: Call<ShoppingCart?>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun showAlertDialog() {
        // Create an AlertDialog.Builder object
        val builder = AlertDialog.Builder(requireContext())

        // Set the title and message of the dialog
        builder.setTitle("Error")
            .setMessage("Cannot add item from different shop.")

        // Set a positive button and its click listener
        builder.setPositiveButton("OK") { dialog, which ->
            // Nothing Happens
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }
}