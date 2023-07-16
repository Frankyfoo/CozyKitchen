package com.example.cozykitchen.chef.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.chef.activity.ChefMainActivity
import com.example.cozykitchen.databinding.FragmentManageFoodBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.model.Product
import com.example.cozykitchen.model.Shop
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ManageFoodFragment : Fragment() {

    private lateinit var binding: FragmentManageFoodBinding
    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var etIngredients: EditText
    private lateinit var btnAddFood: Button
    private lateinit var btnUpdateFood: Button
    private lateinit var btnDeleteFood: Button

    private lateinit var imageFood: ImageView
    private lateinit var btnAddImage: Button
    private lateinit var selectedImageUri: Uri
    private var imageUrlString: String? = null
    private val PICK_IMAGE_REQUEST = 1

    private val storageRef = FirebaseStorage.getInstance().reference

    private lateinit var product: Product
    private var isCardClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize firebase
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        isCardClick = arguments?.getBoolean("IsCardClick", false) == true

        // Set the title in the app bar
        if (!isCardClick) {
            (activity as AppCompatActivity).supportActionBar?.title = "Add Food"
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Manage Food"
        }

        binding = FragmentManageFoodBinding.inflate(layoutInflater)
        imageFood = binding.imgFood
        btnAddImage = binding.btnSelectImage
        etName = binding.etName
        etDescription = binding.etDescription
        etPrice = binding.etPrice
        etIngredients = binding.etIngredients
        btnAddFood = binding.btnAddFood
        btnUpdateFood = binding.btnUpdateFood
        btnDeleteFood = binding.btnDeleteFood

        // loads food information
        if (isCardClick) {
            //disable add function
            btnAddFood.visibility = View.GONE
            val productId = arguments?.getString("ProductId", null)

            if (productId != null) {
                KitchenApi.retrofitService.getFoodById(productId).enqueue(object: Callback<Product?>{
                    override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                        if(response.isSuccessful) {
                            product = response.body()!!
                            // populate fields with data
                            populateFieldsWithData()
                        }
                    }

                    override fun onFailure(call: Call<Product?>, t: Throwable) {
                        Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun populateFieldsWithData() {
        etName.setText(product.productName)
        etDescription.setText(product.productDescription)
        etPrice.setText(product.productPrice.toString())
        etIngredients.setText(product.productIngredients)
        Glide.with(requireContext())
            .load(product.productUrl)
            .into(imageFood)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This is when chef wants to add a food
        if (!isCardClick) {
            btnDeleteFood.visibility = View.GONE
            btnUpdateFood.visibility = View.GONE
        }

        val shopId = arguments?.getString("ShopId")

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // put image
        btnAddImage.setOnClickListener {
            openGallery()
        }

        btnDeleteFood.setOnClickListener {
            val productId = arguments?.getString("ProductId", null)

            KitchenApi.retrofitService.deleteFood(productId!!).enqueue(object: Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    Toast.makeText(requireContext(), "Food deleted successfully.", Toast.LENGTH_SHORT).show()
                    // go back to previous screen
                    findNavController().navigateUp()
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

        btnAddFood.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val ingredients = etIngredients.text.toString().trim()
            var isNotValid = false

            if (name.isEmpty()) {
                etName.error = "Required Field"
                etName.requestFocus()
                isNotValid = true
            }

            if (description.isEmpty()) {
                etDescription.error = "Required Field"
                etDescription.requestFocus()
                isNotValid = true
            }

            if (price.isEmpty()) {
                etPrice.error = "Required Field"
                etPrice.requestFocus()
                isNotValid = true
            }

            if (ingredients.isEmpty()) {
                etIngredients.error = "Required Field"
                etIngredients.requestFocus()
                isNotValid = true
            }

            if (!isNotValid) {
                uploadImage { imageUrl ->
                    if (imageUrl != null) {
                        imageUrlString = imageUrl
//                        Toast.makeText(requireContext(), "$imageUrlString.", Toast.LENGTH_SHORT).show()

                        val foodData = shopId?.let { id ->
                            Product(
                                "test", name, description, ingredients, imageUrl, price.toFloat(),  true, id
                            )
                        }

                        val foodDataBody = RequestBodySingleton.makeGSONRequestBody(foodData)

                        KitchenApi.retrofitService.addFood(foodDataBody).enqueue(object: Callback<Product>{
                            override fun onResponse(
                                call: Call<Product>,
                                response: Response<Product>
                            ) {
                                Toast.makeText(requireContext(), "Food created successfully.", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(call: Call<Product>, t: Throwable) {
                                Toast.makeText(requireContext(), "${t.message}.", Toast.LENGTH_SHORT).show()
                            }

                        })

                    } else {
                        Toast.makeText(requireContext(), "No image uploaded.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnUpdateFood.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val ingredients = etIngredients.text.toString().trim()
            var isNotValid = false

            if (name.isEmpty()) {
                etName.error = "Required Field"
                etName.requestFocus()
                isNotValid = true
            }

            if (description.isEmpty()) {
                etDescription.error = "Required Field"
                etDescription.requestFocus()
                isNotValid = true
            }

            if (price.isEmpty()) {
                etPrice.error = "Required Field"
                etPrice.requestFocus()
                isNotValid = true
            }

            if (ingredients.isEmpty()) {
                etIngredients.error = "Required Field"
                etIngredients.requestFocus()
                isNotValid = true
            }

            if (!isNotValid) {
                uploadImage { imageUrl ->
                    if (imageUrl != null) {
                        imageUrlString = imageUrl
                        // Update the fields of the existing product object
                        product.productName = name
                        product.productDescription = description
                        product.productPrice = price.toFloat()
                        product.productIngredients = ingredients
                        product.productUrl = imageUrl

                        val foodDataBody = RequestBodySingleton.makeGSONRequestBody(product)

                        KitchenApi.retrofitService.updateFood(product.productId, foodDataBody).enqueue(object: Callback<Product>{
                            override fun onResponse(
                                call: Call<Product>,
                                response: Response<Product>
                            ) {
                                Toast.makeText(requireContext(), "Food updated successfully.", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(call: Call<Product>, t: Throwable) {
                                Toast.makeText(requireContext(), "${t.message}.", Toast.LENGTH_SHORT).show()
                            }

                        })

                    } else {
                        // No image is selected, update the food with the existing image URL
                        product.productName = name
                        product.productDescription = description
                        product.productPrice = price.toFloat()
                        product.productIngredients = ingredients

                        val foodDataBody = RequestBodySingleton.makeGSONRequestBody(product)

                        KitchenApi.retrofitService.updateFood(product.productId, foodDataBody).enqueue(object: Callback<Product>{
                            override fun onResponse(
                                call: Call<Product>,
                                response: Response<Product>
                            ) {
                                Toast.makeText(requireContext(), "Food updated successfully.", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(call: Call<Product>, t: Throwable) {
                                Toast.makeText(requireContext(), "${t.message}.", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun saveImageToFile(imageUri: Uri): Uri {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val fileName = "${dateFormat.format(Date(currentTime))}.jpg" // Generate a unique file name
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val outputFile = File(requireContext().filesDir, fileName) // Replace "image.jpg" with a desired file name
        val outputStream = FileOutputStream(outputFile)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return Uri.fromFile(outputFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri= data?.data!!

            // Use the selected image URI as needed (e.g., display in an ImageView)
            imageFood.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImage(callback: (imageUrl: String?) -> Unit) {
        // Check if an image is selected
        if (::selectedImageUri.isInitialized) {
            // Save the image file to a location and retrieve its URI or file path
            val imageFileUri = saveImageToFile(selectedImageUri)
            val imageFileString = imageFileUri.lastPathSegment

            // Create a Firebase Storage reference
            val imageRef = storageRef.child("foods/$imageFileString")

            val uploadTask = imageRef.putFile(imageFileUri)

            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Image upload is successful
                    // Retrieve the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // set imageUrl to global variable
                        val imageUrl = downloadUri.toString()
                        callback(imageUrl)
                    }.addOnFailureListener { exception ->
                        // Handle any errors that occurred during download URL retrieval
                        Toast.makeText(requireContext(), "${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Image upload failed
                    val exception = task.exception
                    Toast.makeText(requireContext(), "${exception!!.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }
}