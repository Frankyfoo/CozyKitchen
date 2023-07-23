package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentAddWalletBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.model.Wallet
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWalletFragment : Fragment() {

    private lateinit var binding: FragmentAddWalletBinding
    private lateinit var session: LoginPreference
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnAddWallet: Button

    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get current logged in user ID
        session = LoginPreference(requireContext())
        userId = session.getCurrentUserId()

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Add Wallet"

        binding = FragmentAddWalletBinding.inflate(inflater, container, false)

        etPhoneNumber = binding.etPhoneNumber
        btnAddWallet = binding.btnAddWallet

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddWallet.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            var isNotValid = false

            if (phoneNumber.isEmpty()) {
                etPhoneNumber.error = "Required Field."
                etPhoneNumber.requestFocus()
                isNotValid = true
            } else if (!ValidatorSingleton.isValidMalaysiaPhoneNumber(phoneNumber)) {
                etPhoneNumber.error = "Invalid malaysia number"
                etPhoneNumber.requestFocus()
                isNotValid = true
            }

            if (!isNotValid) {
                val wallet = Wallet( "test", phoneNumber, userId)
                addWallet(RequestBodySingleton.makeGSONRequestBody(wallet))
            }
        }
    }

    private fun addWallet(requestBody: RequestBody) {
        KitchenApi.retrofitService.addNewWallet(requestBody).enqueue(object: Callback<Wallet>{
            override fun onResponse(call: Call<Wallet>, response: Response<Wallet>) {
                Toast.makeText(requireContext(), "Wallet Information Added Successfully", Toast.LENGTH_SHORT).show()

                // return to previous layout
                findNavController().popBackStack()
            }

            override fun onFailure(call: Call<Wallet>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }
}