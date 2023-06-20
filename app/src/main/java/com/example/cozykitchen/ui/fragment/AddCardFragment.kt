package com.example.cozykitchen.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentAddCardBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.ValidatorSingleton
import com.example.cozykitchen.request.PostCard
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AddCardFragment : Fragment() {

    private lateinit var binding: FragmentAddCardBinding
    private lateinit var session: LoginPreference
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etCvcCode: EditText
    private lateinit var btnAddCard: Button

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
        (activity as AppCompatActivity).supportActionBar?.title = "Card"

        binding = FragmentAddCardBinding.inflate(inflater, container, false)

        etCardNumber = binding.etCardNumber
        etExpiryDate = binding.etExpiryDate
        etCvcCode = binding.etCvcCode
        btnAddCard = binding.btnAddCard

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etExpiryDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, _ ->
                // Handle the selected date
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val selectedDate = "$formattedMonth/$selectedYear"
                etExpiryDate.setText(selectedDate)
            }, year, month, 1)

            datePickerDialog.datePicker.minDate = calendar.timeInMillis // Set minimum date as today
            datePickerDialog.show()
        }

        btnAddCard.setOnClickListener {
            val cardNumber = etCardNumber.text.toString().trim()
            val cardExpiryDate = etExpiryDate.text.toString().trim()
            val cvcCode = etCvcCode.text.toString().trim()
            var isNotValid = false

            if (cardNumber.isEmpty() || !ValidatorSingleton.isValidCardNumberLength(cardNumber)) {
                etCardNumber.error = if (cardNumber.isEmpty()) "Card Number Required" else "Exactly 16 Numbers required"
                etCardNumber.requestFocus()
                isNotValid = true
            }

            if (cardExpiryDate.isEmpty()) {
                etExpiryDate.error = "Date Required"
                etExpiryDate.requestFocus()
                isNotValid = true
            }

            if (cvcCode.isEmpty() || !ValidatorSingleton.isValidCvcCodeLength(cvcCode)) {
                etCvcCode.error = if (cvcCode.isEmpty()) "CVC Code Required" else "Exactly 3 Numbers required"
                etCvcCode.requestFocus()
                isNotValid = true
            }

            if (!isNotValid) {
                val card = PostCard( "test", cardNumber, cardExpiryDate, cvcCode, userId)
                addCard(RequestBodySingleton.makeGSONRequestBody(card))
            }
        }
    }

    private fun addCard(requestBody: RequestBody) {
        KitchenApi.retrofitService.addNewCard(requestBody).enqueue(object: Callback<PostCard?>{
            override fun onResponse(call: Call<PostCard?>, response: Response<PostCard?>) {
                Toast.makeText(requireContext(), "Card Information Added Successfully", Toast.LENGTH_SHORT).show()

                // return to previous layout
                findNavController().popBackStack()
            }

            override fun onFailure(call: Call<PostCard?>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}