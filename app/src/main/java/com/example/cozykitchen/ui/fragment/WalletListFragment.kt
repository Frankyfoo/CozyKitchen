package com.example.cozykitchen.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.WalletAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentWalletListBinding
import com.example.cozykitchen.model.Wallet
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface OnWalletClickListener {
    fun onDeleteClick(walletId: String)
}

class WalletListFragment : Fragment(), OnWalletClickListener {

    private lateinit var binding: FragmentWalletListBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: WalletAdapter

    private var walletList: MutableList<Wallet>? = null

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
        (activity as AppCompatActivity).supportActionBar?.title = "Wallet List"

        binding = FragmentWalletListBinding.inflate(inflater, container, false)
        val walletRecyclerView: RecyclerView = binding.walletRecyclerView
        walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        KitchenApi.retrofitService.getWalletsByUserId(userId).enqueue(object: Callback<List<Wallet>>{
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful) {
                    walletList = response.body() as MutableList<Wallet>
                    if (!walletList.isNullOrEmpty()) {
                        adapter = WalletAdapter(walletList!!, this@WalletListFragment)
                        walletRecyclerView.adapter = adapter
                        binding.tvNoWallet.visibility = View.GONE
                    } else {
                        binding.tvNoWallet.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) {
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

        // FAB for adding new Wallet
        binding.fabAddWallet.setOnClickListener {
            findNavController().navigate(R.id.action_walletListFragment_to_addWalletFragment)
        }

    }

    override fun onDeleteClick(walletId: String) {
//        Toast.makeText(requireContext(), "$walletId", Toast.LENGTH_SHORT).show()
        KitchenApi.retrofitService.deleteWallet(walletId).enqueue(object: Callback<Wallet>{
            override fun onResponse(call: Call<Wallet>, response: Response<Wallet>) {
                if (response.isSuccessful) {
                    // wallet deleted successfully, remove it from the list
                    val deletedWallet = findDeletedWalletFromList(walletId)

                    deletedWallet?.let {
                        // Remove the deleted wallet from the list
                        walletList!!.remove(deletedWallet)
                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged()

                        Toast.makeText(requireContext(), "Wallet Deleted Successfully", Toast.LENGTH_SHORT).show()

                        if (walletList!!.isNullOrEmpty()) {
                            binding.tvNoWallet.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Wallet>, t: Throwable) {
                Toast.makeText(requireContext(), "$t", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun findDeletedWalletFromList(walletId: String): Wallet? {
        // Iterate through the walletList and find the deleted wallet using its ID
        for (wallet in walletList!!) {
            if (wallet.walletId == walletId) {
                return wallet
            }
        }
        return null
    }
}