package com.example.cozykitchen.chef.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.ActivityAddShopBinding
import com.example.cozykitchen.helper.RequestBodySingleton
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
import java.util.*

class AddShopActivity : AppCompatActivity(), OnMarkerDragListener {

    private lateinit var binding: ActivityAddShopBinding
    private lateinit var imageShop: ImageView
    private lateinit var btnAddImage: Button
    private lateinit var btnAddShop: Button
    private lateinit var etShopName: EditText
    private lateinit var etShopDescription: EditText
    private lateinit var selectedImageUri: Uri

    private lateinit var session: LoginPreference

    private var imageUrlString: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val PICK_IMAGE_REQUEST = 1

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize firebase
        FirebaseApp.initializeApp(this)

        binding = ActivityAddShopBinding.inflate(layoutInflater)
        imageShop = binding.imgShop
        btnAddImage = binding.btnSelectImage
        btnAddShop = binding.btnAddShop
        etShopName = binding.etShopName
        etShopDescription = binding.etShopDescription

        // Change title
        supportActionBar?.title = "Add new Shop"

        // get current logged in chef ID
        session = LoginPreference(this)
        val chefId = session.getCurrentUserId()

        // Swaps framelayout to Map Fragment
        val fragment = MapFragment()
        fragment.setOnMarkerDragListener(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, fragment)
            .commit()

        setContentView(binding.root)

        // show dialog to select how image are taken
        btnAddImage.setOnClickListener {
            openGallery()
        }

        btnAddShop.setOnClickListener {
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

                        val shopData = Shop(
                            "Test", name, description, imageUrlString!!, latitude, longitude, null
                        )

                        val shopDataBody = RequestBodySingleton.makeGSONRequestBody(shopData)

                        KitchenApi.retrofitService.addShop(shopDataBody, chefId).enqueue(object: Callback<Shop>{
                            override fun onResponse(call: Call<Shop>, response: Response<Shop>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@AddShopActivity, "Shop Added", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@AddShopActivity, ChefMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onFailure(call: Call<Shop>, t: Throwable) {
                                Toast.makeText(this@AddShopActivity, "Error", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }
        }
    }

    // opens gallery of application
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun saveImageToFile(imageUri: Uri): Uri {
        val fileName = "${UUID.randomUUID()}.jpg" // Generate a unique file name
        val inputStream = contentResolver.openInputStream(imageUri)
        val outputFile = File(filesDir, fileName) // Replace "image.jpg" with a desired file name
        val outputStream = FileOutputStream(outputFile)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return Uri.fromFile(outputFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data!!
                    if (selectedImageUri != null) {
                        // Load the image into the ImageView using Glide
                        Glide.with(this)
                            .load(selectedImageUri)
                            .into(imageShop)
                    }
                }
            }
        }
    }

    private fun uploadImage(callback: (imageUrl: String?) -> Unit) {
        // Check if an image is selected
        if (::selectedImageUri.isInitialized) {
            // Save the image file to a location and retrieve its URI or file path
            val imageFileUri = saveImageToFile(selectedImageUri)
            val imageFileString = imageFileUri.lastPathSegment

            // Create a Firebase Storage reference
            val imageRef = storageRef.child("images/$imageFileString")

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
                        Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Image upload failed
                    val exception = task.exception
                    Toast.makeText(this, "${exception!!.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
        } else {
            // No image is selected, display an error message
            Toast.makeText(this, "No Image uploaded.", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }

    override fun onMarkerDragEnd(locationString: String, latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}