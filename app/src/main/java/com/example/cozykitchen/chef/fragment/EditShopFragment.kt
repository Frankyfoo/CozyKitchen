package com.example.cozykitchen.chef.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.cozykitchen.databinding.FragmentEditShopBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.sharedPreference.LoginPreference
import com.example.cozykitchen.ui.fragment.MapFragment
import com.example.cozykitchen.ui.fragment.OnMarkerDragListener
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditShopFragment : Fragment(), OnMarkerDragListener {

    private lateinit var binding: FragmentEditShopBinding
    private lateinit var session: LoginPreference
    private lateinit var imageShop: ImageView
    private lateinit var btnAddImage: Button
    private lateinit var btnUpdateShop: Button
    private lateinit var etShopName: EditText
    private lateinit var etShopDescription: EditText

    private var shopId: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var selectedImageUri: Uri
    private var imageUrlString: String? = null
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var shop: Shop

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize firebase
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Edit Shop"

        // get current userid
        session = LoginPreference(requireContext())
        val chefId = session.getCurrentUserId()

        binding = FragmentEditShopBinding.inflate(inflater, container, false)
        imageShop = binding.imgShop
        btnAddImage = binding.btnSelectImage
        btnUpdateShop = binding.btnUpdateShop
        etShopName = binding.etShopName
        etShopDescription = binding.etShopDescription

        // Load shop information
        KitchenApi.retrofitService.getChefById(chefId).enqueue(object: Callback<Chef> {
            override fun onResponse(call: Call<Chef>, response: Response<Chef>) {
                if (response.isSuccessful) {
                    var chef = response.body()
                    if (chef != null) {
                        shopId = chef.shopId
                        KitchenApi.retrofitService.getShopById(shopId!!).enqueue(object: Callback<Shop>{
                            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                                if (response.isSuccessful) {
                                    shop = response.body()!!
                                    shop?.let {
                                        etShopName.setText(it.shopName)
                                        etShopDescription.setText(it.shopDescription)
                                        Glide.with(requireContext())
                                            .load(it.shopImageUrl)
                                            .into(imageShop)
                                        latitude = it.latitude
                                        longitude = it.longitude

                                        // Swap the Framelayout in this Fragment to MapFragment
                                        val fragment = MapFragment()
                                        fragment.setOnMarkerDragListener(this@EditShopFragment)
                                        fragment.setTargetLocation(it.latitude, it.longitude)
                                        childFragmentManager.beginTransaction()
                                            .replace(R.id.map_fragment_container, fragment)
                                            .commit()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Shop>, t: Throwable) {
                                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }

            override fun onFailure(call: Call<Chef>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        btnAddImage.setOnClickListener {
            openGallery()
        }

        btnUpdateShop.setOnClickListener {
            val name = etShopName.text.toString().trim()
            val description = etShopDescription.text.toString().trim()
            var isNotValid = false

            if (name.isEmpty()) {
                etShopName.error = "Required Field"
                etShopName.requestFocus()
                isNotValid = true
            }

            if (description.isEmpty()) {
                etShopDescription.error = "Required Field"
                etShopDescription.requestFocus()
                isNotValid = true
            }

            if (!isNotValid) {
                uploadImage { imageUrl ->
                    if (imageUrl != null) {
                        imageUrlString = imageUrl
                        // Update the fields of the existing shop object
                        shop.shopName = name
                        shop.shopDescription = description
                        shop.shopImageUrl = imageUrl
                        shop.latitude = latitude
                        shop.longitude = longitude

                        val shopDataBody = RequestBodySingleton.makeGSONRequestBody(shop)

                        KitchenApi.retrofitService.updateShop(shopId!!, shopDataBody).enqueue(object: Callback<Shop>{
                            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                                Toast.makeText(requireContext(), "Shop updated successfully.", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(call: Call<Shop>, t: Throwable) {
                                Toast.makeText(requireContext(), "${t.message}.", Toast.LENGTH_SHORT).show()
                            }

                        })


                    } else {
                        // No image is selected, update the shop with the existing image URL
                        shop.shopName = name
                        shop.shopDescription = description
                        shop.latitude = latitude
                        shop.longitude = longitude

                        val shopDataBody = RequestBodySingleton.makeGSONRequestBody(shop)

                        KitchenApi.retrofitService.updateShop(shopId!!, shopDataBody).enqueue(object: Callback<Shop>{
                            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                                Toast.makeText(requireContext(), "Shop updated successfully.", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(call: Call<Shop>, t: Throwable) {
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
            imageShop.setImageURI(selectedImageUri)
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

    override fun onMarkerDragEnd(locationString: String, latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}