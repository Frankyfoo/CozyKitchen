package com.example.cozykitchen.ui.fragment

import android.content.Context
import android.media.Image
import android.os.Build
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentConfirmPurchaseBinding
import com.example.cozykitchen.helper.RequestBodySingleton
import com.example.cozykitchen.helper.TimeListGenerator
import com.example.cozykitchen.model.Address
import com.example.cozykitchen.model.Card
import com.example.cozykitchen.model.Order
import com.example.cozykitchen.model.Wallet
import com.example.cozykitchen.request.PostOrder
import com.example.cozykitchen.sharedPreference.LoginPreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmPurchaseFragment : Fragment() {

    private lateinit var binding: FragmentConfirmPurchaseBinding

    private lateinit var imgBtnAddress: ImageButton
    private lateinit var imgBtnCard: ImageButton
    private lateinit var imgBtnWallet: ImageButton
    private lateinit var spinnerAddress: Spinner
    private lateinit var spinnerCard: Spinner
    private lateinit var spinnerWallet: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var btnConfirmPurchase: Button
    private lateinit var radioGroupPayment: RadioGroup
    private lateinit var etRemarks: EditText

    private var userId: String? = null
    private var totalCost: Float? = null
    private var shopId: String? = null
    var cardList: MutableList<Card>? = null
    var addressList: MutableList<Address>? = null
    var walletList: MutableList<Wallet>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get UserId and TotalCost from previous screen
        userId = arguments?.getString("UserId")
        totalCost = arguments?.getFloat("TotalCost")
        shopId = arguments?.getString("ShopId")

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Confirm"

        binding = FragmentConfirmPurchaseBinding.inflate(inflater, container, false)

        imgBtnAddress = binding.imgBtnEditAddress
        imgBtnCard = binding.imgBtnEditCard
        imgBtnWallet = binding.imgBtnEditWallet
        spinnerAddress = binding.spinnerAddress
        spinnerCard = binding.spinnerCard
        spinnerWallet = binding.spinnerWallet
        spinnerTime = binding.spinnerTime
        btnConfirmPurchase = binding.btnConfirmPurchase
        radioGroupPayment = binding.rgPaymentMethod
        etRemarks = binding.etRemarks

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

        // get Wallets by UserId
        KitchenApi.retrofitService.getWalletsByUserId(userId!!).enqueue(object: Callback<List<Wallet>>{
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // create a new List of String to populate the spinner
                        val walletNumberList: MutableList<String> = mutableListOf()

                        // cast the response to global variable
                        walletList = body.toMutableList()

                        // add the each card number to List of String
                        for (wallet in walletList!!) {
                            walletNumberList.add(wallet.phoneNumber)
                        }

                        // populate the spinner with String list
                        val spinnerWalletAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, walletNumberList)
                        spinnerWalletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerWallet.adapter = spinnerWalletAdapter
                    } else {
                        // Handle null body
                        Log.d("API", "Response body is null")
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("API", "Unsuccessful response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) {
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
        var walletId: String? = null
        var selectedTimeString = ""
        var paymentType: String? = "CARD"
        var selectedCard: Card? = null
        var selectedWallet: Wallet? = null

        radioGroupPayment.setOnCheckedChangeListener{ group, checkedId ->
            when (checkedId) {
                R.id.rb_card -> {
                    binding.llCard.visibility = View.VISIBLE
                    spinnerCard.visibility = View.VISIBLE
                    binding.llWallet.visibility = View.GONE
                    spinnerWallet.visibility = View.GONE
                    paymentType = "CARD"
//                    selectedCard = cardList?.get(spinnerCard.selectedItemPosition)
                    selectedCard = if (spinnerCard.selectedItemPosition != AdapterView.INVALID_POSITION) {
                        cardList?.get(spinnerCard.selectedItemPosition)
                    } else {
                        null
                    }
                    cardId = selectedCard?.cardId
                    walletId = null
                }
                R.id.rb_wallet -> {
                    binding.llCard.visibility = View.GONE
                    spinnerCard.visibility = View.GONE
                    binding.llWallet.visibility = View.VISIBLE
                    spinnerWallet.visibility = View.VISIBLE
                    paymentType = "WALLET"
//                    selectedWallet = walletList?.get(spinnerWallet.selectedItemPosition)
                    selectedWallet = if (spinnerWallet.selectedItemPosition != AdapterView.INVALID_POSITION) {
                        walletList?.get(spinnerWallet.selectedItemPosition)
                    } else {
                        null
                    }
                    walletId = selectedWallet?.walletId
                    cardId = null
                }
                R.id.rb_cash -> {
                    binding.llCard.visibility = View.GONE
                    spinnerCard.visibility = View.GONE
                    binding.llWallet.visibility = View.GONE
                    spinnerWallet.visibility = View.GONE
                    paymentType = "CASH"
                    selectedCard = null
                    selectedWallet = null
                    cardId = null
                    walletId = null
                }
            }
        }

        // go to card management
        imgBtnCard.setOnClickListener {
            findNavController().navigate(R.id.cardListFragment)
        }

        // go to address management
        imgBtnAddress.setOnClickListener {
            findNavController().navigate(R.id.addressListFragment)
        }

        // go to wallet management
        imgBtnWallet.setOnClickListener {
            findNavController().navigate(R.id.walletListFragment)
        }

        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTimeString = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        // handle card dropdownlist
        spinnerCard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // get the card number showing in the spinner and remove the whitespace
                var cardNumber = parent?.getItemAtPosition(position) as String
                cardNumber = cardNumber.replace(" ", "")
//                Log.d("TestingCardNumber", "$cardNumber")

                // find the card that has the same card number
                var selectedCard = cardList!!.firstOrNull{ it.cardNumber == cardNumber}

                // if found then get the card id
                if (selectedCard != null) {
                    cardId = selectedCard!!.cardId
//                    Log.d("TestingCardGet", "$cardId")
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
//                Log.d("TestingAddress", "$addressName")

                // find the card that has the same card number
                var selectedAddress = addressList!!.firstOrNull{ it.name == addressName}

                // if found then get the address id
                if (selectedAddress != null) {
                    addressId = selectedAddress.addressId
//                    Log.d("TestingAddress", "$addressId")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        // handle Wallet dropdownlist
        spinnerWallet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // get the Wallet Phone Number showing in the spinner
                var walletPhoneNumber = parent?.getItemAtPosition(position) as String
//                Log.d("TestingAddress", "$walletPhoneNumber")

                // find the card that has the same card number
                var selectedWallet = walletList!!.firstOrNull{ it.phoneNumber == walletPhoneNumber}

                // if found then get the address id
                if (selectedWallet != null) {
                    walletId = selectedWallet!!.walletId
//                    Log.d("TestingWallet", "$walletId")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }

        btnConfirmPurchase.setOnClickListener {
            if (paymentType == null || (paymentType == "CARD" && cardId.isNullOrEmpty()) || (paymentType == "WALLET" && walletId.isNullOrEmpty()) || addressId.isNullOrEmpty()) {
                if (addressId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please add or select a delivery address.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please add or select a valid payment option.", Toast.LENGTH_SHORT).show()
                }

            } else {
                val remarks = etRemarks.text.toString().trim()
                var isNotValid = false

                if (!isNotValid) {
                    val order = PostOrder(
                        "test",
                        totalCost!!,
                        "PAY_COMPLETED",
                        userId!!,
                        addressId!!,
                        cardId,
                        walletId,
                        paymentType!!,
                        remarks,
                        selectedTimeString,
                        shopId
                    )
                    confirmPurchase(RequestBodySingleton.makeGSONRequestBody(order))
                }
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