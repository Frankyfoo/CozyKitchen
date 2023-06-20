package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.example.cozykitchen.databinding.FragmentCardListBinding
import com.example.cozykitchen.sharedPreference.LoginPreference

class CardListFragment : Fragment() {

    private lateinit var binding: FragmentCardListBinding
    private lateinit var session: LoginPreference

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
}