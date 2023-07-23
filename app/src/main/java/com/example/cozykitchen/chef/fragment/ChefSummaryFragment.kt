package com.example.cozykitchen.chef.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozykitchen.R
import com.example.cozykitchen.adapter.SummaryAdapter
import com.example.cozykitchen.api.KitchenApi
import com.example.cozykitchen.databinding.FragmentChefSummaryBinding
import com.example.cozykitchen.helper.TimeListGenerator
import com.example.cozykitchen.model.Chef
import com.example.cozykitchen.model.Summary
import com.example.cozykitchen.sharedPreference.LoginPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChefSummaryFragment : Fragment() {
    private lateinit var binding: FragmentChefSummaryBinding
    private lateinit var session: LoginPreference
    private lateinit var adapter: SummaryAdapter
    private lateinit var spinnerTime: Spinner
    private lateinit var summaryRecyclerView: RecyclerView

    private var shopId: String? = null
    private var selectedDateTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set the title in the app bar
        (activity as AppCompatActivity).supportActionBar?.title = "Summary"

        binding = FragmentChefSummaryBinding.inflate(inflater, container, false)
        spinnerTime = binding.spinnerTime

        summaryRecyclerView = binding.rvSummary
        summaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // get chef Id and shop Id
        session = LoginPreference(requireContext())
        val chefId = session.getCurrentUserId()
        shopId = session.getShopId()

        // generate 3 days in advanced pre-ordering time
        val timeListString: MutableList<String> = mutableListOf()
        val timeList = TimeListGenerator().generateTimeList("Summary")

        for (time in timeList) {
            val timeString = time.text
            timeListString.add(timeString)
        }

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeListString)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTime.adapter = spinnerAdapter

        if (shopId.isNullOrEmpty()) {
            binding.spinnerTime.isEnabled = false
            binding.rvSummary.visibility = View.GONE
            binding.tvNoSummary.text = "You have not created a shop."
            binding.tvNoSummary.setTextColor(R.color.grey)
            binding.tvNoSummary.visibility = View.VISIBLE
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close the application when back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        // Hide the back button in the app bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // updates recycler view based on pressed item
        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDateTime = parent?.getItemAtPosition(position) as String

                val selectedDateTime = selectedDateTime ?: ""
                val shopId = shopId ?: ""

                KitchenApi.retrofitService.getOrderSummary(selectedDateTime, shopId).enqueue(object: Callback<List<Summary>>{
                    override fun onResponse(call: Call<List<Summary>>, response: Response<List<Summary>>) {
                        if (response.isSuccessful) {
                            val summaries = response.body()
                            if(!summaries.isNullOrEmpty()) {
                                binding.spinnerTime.visibility = View.VISIBLE
                                binding.rvSummary.visibility = View.VISIBLE
                                binding.tvNoSummary.visibility = View.GONE

                                adapter = SummaryAdapter(summaries)
                                summaryRecyclerView.adapter = adapter
                            } else {
                                binding.rvSummary.visibility = View.GONE
                                binding.tvNoSummary.text = "You do not have any order at this time."
                                binding.tvNoSummary.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Summary>>, t: Throwable) {
                        Log.d("Testing", "${t.message}")
                    }

                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                Log.d("TestingNotSelected", "No selection")
            }
        }
    }
}