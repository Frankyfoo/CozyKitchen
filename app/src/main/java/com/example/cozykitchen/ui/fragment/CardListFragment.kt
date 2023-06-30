package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.CardAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentCardListBinding
import com.example.cozykitchen.model.Card
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnCardClickListener {
    fun onDeleteClick(cardId: String)
}

class CardListFragment : Fragment(), OnCardClickListener {

    private lateinit var binding: FragmentCardListBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: CardAdapter

    private var cardList: MutableList<Card>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get current logged in user ID
        session = LoginPreference(requireContext())
        val userId = session.getCurrentUserId()

        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Card List"

        binding = FragmentCardListBinding.inflate(inflater, container, false)
        val cardRecyclerView: RecyclerView = binding.cardRecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getCardsByUserId(userId).enqueue(object: Callback<List<Card>>{
            override fun onResponse(call: Call<List<Card>>, response: Response<List<Card>>) {
                if (response.isSuccessful) {
                    cardList = response.body() as MutableList<Card>?
                    if (!cardList.isNullOrEmpty()) {
                        adapter = CardAdapter(cardList!!, this@CardListFragment)
                        cardRecyclerView.adapter = adapter

                        binding.tvNoCard.visibility = View.GONE
                    } else {
                        binding.tvNoCard.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<List<Card>>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
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

        // FAB for adding new card
        binding.fabAddCard.setOnClickListener{
            findNavController().navigate(R.id.action_cardListFragment_to_addCardFragment)
        }
    }

    override fun onDeleteClick(cardId: String) {
        KitchenApi.retrofitService.deleteCard(cardId).enqueue(object: Callback<Card>{
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                if (response.isSuccessful) {

                    // card deleted successfully, remove it from the list
                    val deletedCard = findDeletedCardFromList(cardId)

                    deletedCard?.let {
                        // Remove the deleted card from the list
                        cardList!!.remove(deletedCard)
                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Card Deleted Successfully", Toast.LENGTH_SHORT).show()

                        if (cardList!!.isNullOrEmpty()) {
                            binding.tvNoCard.visibility = View.VISIBLE
                        }
                    }

                }
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun findDeletedCardFromList(cardId: String): Card? {
        // Iterate through the cardList and find the deleted card using its ID
        for (card in cardList!!) {
            if (card.cardId == cardId) {
                return card
            }
        }
        return null
    }
}