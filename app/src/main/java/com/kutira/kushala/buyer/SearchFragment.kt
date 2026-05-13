package com.kutira.kushala.buyer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kutira.kushala.adapter.SellerCardAdapter
import com.kutira.kushala.databinding.FragmentBuyerSearchBinding
import com.kutira.kushala.detail.SellerDetailActivity
import com.kutira.kushala.viewmodel.BuyerViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentBuyerSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BuyerViewModel
    private lateinit var adapter: SellerCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[BuyerViewModel::class.java]

        adapter = SellerCardAdapter { seller ->
            val intent = Intent(requireContext(), SellerDetailActivity::class.java)
            intent.putExtra("SELLER_ID", seller.userId)
            startActivity(intent)
        }
        binding.rvSearchResults.adapter = adapter

        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase()
            viewModel.sellers.value?.let { sellers ->
                val filtered = sellers.filter {
                    it.businessName.lowercase().contains(query) ||
                            it.skillArea.lowercase().contains(query) ||
                            it.district.lowercase().contains(query)
                }
                adapter.submitList(filtered)
            }
        }

        viewModel.sellers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}