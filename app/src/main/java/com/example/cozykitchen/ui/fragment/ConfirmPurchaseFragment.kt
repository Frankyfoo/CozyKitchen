package com.example.cozykitchen.ui.fragment

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentConfirmPurchaseBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.model.Card
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.request.PostOrder
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmPurchaseFragment : Fragment() {

    private lateinit var binding: FragmentConfirmPurchaseBinding
//    private lateinit var session: LoginPreference

    private lateinit var imgBtnAddress: ImageButton
    private lateinit var imgBtnCard: ImageButton
    private lateinit var spinnerAddress: Spinner
    private lateinit var spinnerCard: Spinner
    private lateinit var btnConfirmPurchase: Button

    private var userId: String? = null
    private var totalCost: Float? = null
    var cardList: MutableList<Card>? = null
    var addressList: MutableList<Address>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get UserId and TotalCost from previous screen
        userId = arguments?.getString("UserId")
        totalCost = arguments?.getFloat("TotalCost")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Confirm"

        binding = FragmentConfirmPurchaseBinding.inflate(inflater, container, false)

        imgBtnAddress = binding.imgBtnEditAddress
        imgBtnCard = binding.imgBtnEditCard
        spinnerAddress = binding.spinnerAddress
        spinnerCard = binding.spinnerCard
        btnConfirmPurchase = binding.btnConfirmPurchase

        // get Cards by UserID
        KitchenApi.retrofitService.getCardsByUserId(userId!!).enqueue(object: Callback<List<Card>>{
            override fun onResponse(call: Call<List<Card>>, response: Response<List<Card>>) {
//                Log.d("API", "${response.body()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // create a new List of String to populate the spinner
                        val cardNumberList: MutableList<String> = mutableListOf()

                        // cast the response to global variable
                        cardList = body.toMutableList()
//                        Log.d("CardList", "$cardList")

                        // add the each card number to List of String
                        for (card in cardList!!) {
                            cardNumberList.add(formatCardNumber(card.cardNumber!!))
                        }

                        // populate the spinner with String list
                        val spinnerCardAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cardNumberList)
                        spinnerCardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCard.adapter = spinnerCardAdapter
                    } else {
                        // Handle null body
                        Log.d("API", "Response body is null")
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("API", "Unsuccessful response: ${response.code()}")
                }

            }

            override fun onFailure(call: Call<List<Card>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

        })

        // get Addresses by UserID
        KitchenApi.retrofitService.getAddressesByUserId(userId!!).enqueue(object: Callback<List<Address>>{
            override fun onResponse(call: Call<List<Address>>, response: Response<List<Address>>) {
//                Log.d("API", "${response.body()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // create a new List of String to populate the spinner
                        val addressNameList: MutableList<String> = mutableListOf()

                        // cast the response to global variable
                        addressList = body.toMutableList()

                        // add the each card number to List of String
                        for (address in addressList!!) {
                            addressNameList.add(address.name)
                        }

                        // populate the spinner with String list
                        val spinnerAddressAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addressNameList)
                        spinnerAddressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerAddress.adapter = spinnerAddressAdapter
                    } else {
                        // Handle null body
                        Log.d("API", "Response body is null")
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("API", "Unsuccessful response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Address>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var addressId: String? = null
        var cardId: String? = null

        // go to card management
        imgBtnCard.setOnClickListener {
            findNavController().navigate(R.id.cardListFragment)
        }

        // go to address management
        imgBtnAddress.setOnClickListener {
            findNavController().navigate(R.id.addressListFragment)
        }

        // handle card dropdownlist
        spinnerCard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // get the card number showing in the spinner and remove the whitespace
                var cardNumber = parent?.getItemAtPosition(position) as String
                cardNumber = cardNumber.replace(" ", "")
                Log.d("TestingCardNumber", "$cardNumber")

                // find the card that has the same card number
                var selectedCard = cardList!!.firstOrNull{ it.cardNumber == cardNumber}

                // if found then get the card id
                if (selectedCard != null) {
                    cardId = selectedCard.cardId
                    Log.d("TestingCardGet", "$cardId")
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        // handle address dropdownlist
        spinnerAddress.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // get the Address Name showing in the spinner
                var addressName = parent?.getItemAtPosition(position) as String
                Log.d("TestingAddress", "$addressName")

                // find the card that has the same card number
                var selectedAddress = addressList!!.firstOrNull{ it.name == addressName}

                // if found then get the address id
                if (selectedAddress != null) {
                    addressId = selectedAddress.addressId
                    Log.d("TestingAddress", "$addressId")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        btnConfirmPurchase.setOnClickListener {
            if (cardId.isNullOrEmpty() || addressId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select a Card or an Address.", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(requireContext(), "$cardId, $addressId $userId $totalCost PAY_COMPLETED", Toast.LENGTH_SHORT).show()

                val order = PostOrder("test", totalCost!!, "PAY_COMPLETED", userId!!, addressId!!, cardId!!)

                confirmPurchase(RequestBodySingleton.makeGSONRequestBody(order))
            }
        }

        // Go back to previous page
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun confirmPurchase(requestBody: RequestBody) {
        KitchenApi.retrofitService.addOrder(requestBody).enqueue(object: Callback<PostOrder>{
            override fun onResponse(call: Call<PostOrder>, response: Response<PostOrder>) {
                Toast.makeText(requireContext(), "Order has been made successfully.", Toast.LENGTH_SHORT).show()

                // return to shop page
                findNavController().navigate(R.id.shop_fragment)
            }

            override fun onFailure(call: Call<PostOrder>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun formatCardNumber(cardNumber: String): String {
        if (cardNumber.isNullOrEmpty()) {
            return cardNumber
        }

        val result = StringBuilder()
        for (i in cardNumber.indices) {
            if (i > 0 && i % 4 == 0) {
                result.append(" ")
            }
            result.append(cardNumber[i])
        }

        return result.toString()
    }
}