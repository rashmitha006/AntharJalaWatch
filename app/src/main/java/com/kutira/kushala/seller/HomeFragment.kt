package com.kutira.kushala.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.kutira.kushala.R
import com.kutira.kushala.adapter.InquiryAdapter
import com.kutira.kushala.databinding.FragmentSellerHomeBinding
import com.kutira.kushala.repository.BuyerRepository
import com.kutira.kushala.viewmodel.SellerViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentSellerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SellerViewModel
    private lateinit var inquiryAdapter: InquiryAdapter
    private val buyerRepo = BuyerRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SellerViewModel::class.java]

        setupInquiryRecyclerView()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.loadSellerData(userId)
            viewModel.loadProducts(userId) // Load products to update the count
            listenForInquiries(userId)
        }

        viewModel.seller.observe(viewLifecycleOwner) { seller ->
            seller?.let {
                binding.tvWelcome.text = "Namaste, ${it.businessName}!"
                binding.tvShopStatus.text = if (it.isAvailable) "Your shop is OPEN" else "Your shop is CLOSED"
                binding.tvProfileViews.text = it.profileViews.toString()
            }
        }

        // Observe products to update the total count card
        viewModel.products.observe(viewLifecycleOwner) { products ->
            binding.tvTotalProducts.text = products.size.toString()
        }

        binding.btnAddProductQuick.setOnClickListener {
            findNavController().navigate(R.id.navigation_products)
        }

        binding.btnUpdateCapacityQuick.setOnClickListener {
            findNavController().navigate(R.id.navigation_capacity)
        }
    }

    private fun setupInquiryRecyclerView() {
        inquiryAdapter = InquiryAdapter { inquiry ->
            Toast.makeText(context, "Contact Buyer: ${inquiry.buyerMobile}", Toast.LENGTH_LONG).show()
        }
        binding.rvInquiries.layoutManager = LinearLayoutManager(context)
        binding.rvInquiries.adapter = inquiryAdapter
    }

    private fun listenForInquiries(sellerId: String) {
        buyerRepo.getInquiriesForSeller(sellerId).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val list = snapshot?.toObjects(com.kutira.kushala.model.Inquiry::class.java) ?: emptyList()
            inquiryAdapter.submitList(list)
            binding.tvTotalOrders.text = list.size.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.loadProducts(userId) // Ensure count is fresh on resume
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}