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
    private lateinit var btnPlus: Button
    private lateinit var tvQuantity: TextView
    private lateinit var btnMinus: Button
    private lateinit var btnAddToCart: Button
    private lateinit var spinnerTime: Spinner

    private var price: Float = 0f
    private var quantity: Int = 1
    private var size: String = "Normal"
    private var productId: String = ""
    private var currentUserId: String = ""
    private var customizationText: String = ""
    private var selectedTimeString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        tvIngredients = binding.tvIngredients
        tvPrice = binding.tvPrice
        btnMinus = binding.btnMinus
        btnPlus = binding.btnPlus
        btnAddToCart = binding.btnAddToCart
        tvQuantity = binding.tvQuantity
        etCustomization = binding.etCustomization
        spinnerTime = binding.spinnerTime

        // generate 3 days in advanced pre-ordering time
        val timeListString: MutableList<String> = mutableListOf()
        val timeList = TimeListGenerator().generateTimeList()


        for (time in timeList) {
            val timeString = time.text
            timeListString.add(timeString)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeListString)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTime.adapter = adapter

        if (productId != null) {
            KitchenApi.retrofitService.getFoodById(productId).enqueue(object: Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {

                    val product = response.body()
//                    Log.d("Testing", "$product")

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
                }
                R.id.radio_button_large -> {
                    tvPrice.text = "RM ${price + 2.00f}"
                    size = "Large"
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
            }
        }

        // increase quantity of product
        btnPlus.setOnClickListener {
            quantity += 1
            tvQuantity.text = "$quantity"
        }

        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTimeString = parent?.getItemAtPosition(position) as String
//                Log.d("TestingSelected", "$selectedTimeString")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        btnAddToCart.setOnClickListener {
            // get customization text and do some handling
            customizationText = etCustomization.text.toString()

            val cartItem = ShoppingCart(
                "Test", currentUserId, productId, size, quantity, customizationText, selectedTimeString, "PAY_PENDING", null, null
            )

            val cartRequestBody = RequestBodySingleton.makeGSONRequestBody(cartItem)

            KitchenApi.retrofitService.addProductToCart(cartRequestBody).enqueue(object: Callback<ShoppingCart?> {
                override fun onResponse(
                    call: Call<ShoppingCart?>,
                    response: Response<ShoppingCart?>
                ) {
                    Toast.makeText(requireContext(), "Item Added Successfully", Toast.LENGTH_SHORT).show()

                    // return to foodList Fragment
                    findNavController().popBackStack()
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
}