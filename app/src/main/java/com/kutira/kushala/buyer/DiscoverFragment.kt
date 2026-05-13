package com.kutira.kushala.buyer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.kutira.kushala.adapter.SellerCardAdapter
import com.kutira.kushala.databinding.FragmentBuyerDiscoverBinding
import com.kutira.kushala.detail.SellerDetailActivity
import com.kutira.kushala.viewmodel.BuyerViewModel

class DiscoverFragment : Fragment() {
    private var _binding: FragmentBuyerDiscoverBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BuyerViewModel
    private lateinit var availableAdapter: SellerCardAdapter
    private lateinit var newAdapter: SellerCardAdapter
    private var currentCategory: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[BuyerViewModel::class.java]

        availableAdapter = SellerCardAdapter { seller ->
            openSellerDetail(seller.userId)
        }
        newAdapter = SellerCardAdapter { seller ->
            openSellerDetail(seller.userId)
        }

        binding.rvAvailableSellers.adapter = availableAdapter
        binding.rvNewSellers.adapter = newAdapter

        viewModel.loadAllSellers()

        viewModel.sellers.observe(viewLifecycleOwner) { sellers ->
            filterSellers(sellers)
        }

        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.firstOrNull() ?: -1)
            currentCategory = chip?.text?.toString() ?: "All"
            viewModel.sellers.value?.let { filterSellers(it) }
        }
    }

    private fun filterSellers(sellers: List<com.kutira.kushala.model.Seller>) {
        val filtered = if (currentCategory == "All") {
            sellers
        } else {
            sellers.filter { it.productCategories.contains(currentCategory) || it.skillArea.equals(currentCategory, ignoreCase = true) }
        }

        availableAdapter.submitList(filtered.filter { it.isAvailable })
        newAdapter.submitList(filtered.sortedByDescending { it.createdAt })
    }

    private fun openSellerDetail(sellerId: String) {
        val intent = Intent(requireContext(), SellerDetailActivity::class.java)
        intent.putExtra("SELLER_ID", sellerId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}