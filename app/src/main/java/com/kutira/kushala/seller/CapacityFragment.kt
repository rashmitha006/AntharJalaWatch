package com.kutira.kushala.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kutira.kushala.R
import com.kutira.kushala.databinding.FragmentSellerCapacityBinding
import com.kutira.kushala.model.Seller
import com.kutira.kushala.viewmodel.SellerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CapacityFragment : Fragment() {
    private var _binding: FragmentSellerCapacityBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SellerViewModel
    private var currentUnits = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerCapacityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SellerViewModel::class.java]

        setupChart()

        viewModel.seller.observe(viewLifecycleOwner) { seller ->
            seller?.let {
                updateUI(it)
            }
        }

        binding.btnPlus.setOnClickListener {
            currentUnits += 10
            binding.tvUnitsCount.text = currentUnits.toString()
        }

        binding.btnMinus.setOnClickListener {
            if (currentUnits >= 10) {
                currentUnits -= 10
                binding.tvUnitsCount.text = currentUnits.toString()
            }
        }

        binding.btnUpdateCapacity.setOnClickListener {
            val currentSeller = viewModel.seller.value ?: run {
                Toast.makeText(context, "Seller data not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMonths = mutableListOf<Int>()
            for (i in 0 until binding.gridMonths.childCount) {
                val view = binding.gridMonths.getChildAt(i)
                if (view is CheckBox && view.isChecked) {
                    view.tag?.toString()?.toIntOrNull()?.let { month ->
                        selectedMonths.add(month)
                    }
                }
            }

            val seller = currentSeller.copy(
                isAvailable = binding.switchAvailability.isChecked,
                dailyCapacity = binding.etDailyCapacity.text.toString().toIntOrNull() ?: 0,
                unitsAvailableThisWeek = currentUnits,
                leadTimeDays = binding.etLeadTime.text.toString().toIntOrNull() ?: 0,
                acceptsAdvance = binding.switchAdvanceBooking.isChecked,
                activeMonths = selectedMonths,
                updatedAt = System.currentTimeMillis()
            )
            viewModel.updateCapacity(seller)
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            status?.let { (success, message) ->
                if (success) {
                    Toast.makeText(context, "Capacity updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Update failed: $message", Toast.LENGTH_SHORT).show()
                }
                viewModel.resetUpdateStatus()
            }
        }
    }

    private fun setupChart() {
        binding.capacityChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setHoleColor(android.R.color.transparent)
            setCenterTextSize(14f)
        }
    }

    private fun updateUI(seller: Seller) {
        binding.switchAvailability.isChecked = seller.isAvailable
        binding.etDailyCapacity.setText(seller.dailyCapacity.toString())
        currentUnits = seller.unitsAvailableThisWeek
        binding.tvUnitsCount.text = currentUnits.toString()
        binding.etLeadTime.setText(seller.leadTimeDays.toString())
        binding.switchAdvanceBooking.isChecked = seller.acceptsAdvance
        
        // Update months checkboxes
        for (i in 0 until binding.gridMonths.childCount) {
            val view = binding.gridMonths.getChildAt(i)
            if (view is CheckBox) {
                val month = view.tag?.toString()?.toIntOrNull()
                view.isChecked = month != null && seller.activeMonths.contains(month)
            }
        }

        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        binding.tvLastUpdated.text = "Last updated: ${sdf.format(Date(seller.updatedAt))}"

        updateChart(seller)
    }

    private fun updateChart(seller: Seller) {
        val entries = mutableListOf<PieEntry>()
        val available = seller.unitsAvailableThisWeek.toFloat()
        val totalCapacity = (seller.dailyCapacity * 7).toFloat()
        
        if (totalCapacity > 0) {
            val used = (totalCapacity - available).coerceAtLeast(0f)
            entries.add(PieEntry(available, "Available"))
            entries.add(PieEntry(used, "Used/Buffer"))
            
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = listOf(
                resources.getColor(R.color.secondary, null),
                resources.getColor(R.color.accent, null)
            )
            dataSet.setDrawValues(false)
            
            val data = PieData(dataSet)
            binding.capacityChart.data = data
            val percent = if (totalCapacity > 0) ((available/totalCapacity)*100).toInt() else 0
            binding.capacityChart.centerText = "$percent%\nAvailable"
            binding.capacityChart.invalidate()
        } else {
            binding.capacityChart.clear()
            binding.capacityChart.centerText = "Set Daily\nCapacity"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}