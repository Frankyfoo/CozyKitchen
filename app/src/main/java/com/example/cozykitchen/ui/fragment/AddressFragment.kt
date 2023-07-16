package com.example.cozykitchen.ui.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentAddressBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.request.PostAddress
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressFragment : Fragment(), OnMarkerDragListener {

    private lateinit var binding: FragmentAddressBinding
    private lateinit var session: LoginPreference
    private lateinit var tvStreet: TextView
    private lateinit var etStreetName: EditText
    private lateinit var etStreetNumber: EditText
    private lateinit var etPostalCode: EditText
    private lateinit var etInstruction: EditText
    private lateinit var btnAddAddress: Button


    private var userId = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
//    private var addressId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Address Management"

        // get current userid
        session = LoginPreference(requireContext())
        userId = session.getCurrentUserId()

//        addressId = arguments?.getString("AddressId")

        binding = FragmentAddressBinding.inflate(inflater, container, false)
        tvStreet = binding.tvStreet
        etStreetNumber = binding.etStreetNumber
        etPostalCode = binding.etPostalCode
        etInstruction = binding.etInstruction
        btnAddAddress = binding.btnSaveAddress
        etStreetName = binding.etStreetName


        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Swap the Framelayout in this Fragment to MapFragment
        val fragment = MapFragment()
        fragment.setOnMarkerDragListener(this)
        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, fragment)
            .commit()

//        Log.d("ABC", "$addressId")

//        if (!addressId.isNullOrEmpty()) {
//            KitchenApi.retrofitService.getAddressById(addressId!!).enqueue(object : Callback<Address?> {
//                override fun onResponse(call: Call<Address?>, response: Response<Address?>) {
//                    val address =response.body()
////                    Log.d("Testing", "$address")
//                }
//
//                override fun onFailure(call: Call<Address?>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//        }

        btnAddAddress.setOnClickListener {
            val street = tvStreet.text.toString().trim()
            val streetName = etStreetName.text.toString().trim()
            val streetNumber = etStreetNumber.text.toString().trim()
            val postalCode = etPostalCode.text.toString().trim()
            val instruction = etInstruction.text.toString().trim()
            var isNotValid = false

            if (streetNumber.isEmpty()) {
                etStreetNumber.error = "Text Required"
                etStreetNumber.requestFocus()
                isNotValid = true
            }

            if (postalCode.isEmpty()) {
                etPostalCode.error = "Text Required"
                etPostalCode.requestFocus()
                isNotValid = true
            }

            if (instruction.isEmpty()) {
                etInstruction.error = "Text Required"
                etInstruction.requestFocus()
                isNotValid = true
            }

            if (streetName.isEmpty()) {
                etStreetName.error = "Text Required"
                etStreetName.requestFocus()
                isNotValid = true
            }

            // The addressId is a dummy text, it is needed for the POST Request,
            // it will not affect the id generated as the id generation is done
            // in the backend
            if(!isNotValid) {
                if (latitude == 0.0 || longitude == 0.0 || street.isNullOrEmpty()) {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("Unable to find your location.")
                    alertDialogBuilder.setMessage("Make sure location access is granted.")
                    alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                } else {
                    val address = PostAddress(
                        "test", streetName, street, streetNumber, postalCode, latitude, longitude, instruction, userId
                    )

                    addAddress(RequestBodySingleton.makeGSONRequestBody(address))
                }
            }
        }
    }

    private fun addAddress(requestBody: RequestBody) {
        KitchenApi.retrofitService.addNewAddress(requestBody).enqueue(object: Callback<PostAddress?>{
            override fun onResponse(call: Call<PostAddress?>, response: Response<PostAddress?>) {
                Toast.makeText(requireContext(), "Address Added Successfully", Toast.LENGTH_SHORT).show()

                // return to previous Fragment
                findNavController().popBackStack()
            }

            override fun onFailure(call: Call<PostAddress?>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMarkerDragEnd(locationString: String, latitude: Double, longitude: Double) {
        tvStreet.text = locationString
        this.latitude = latitude
        this.longitude = longitude
    }
}