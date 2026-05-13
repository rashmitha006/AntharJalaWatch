package com.kutira.kushala.seller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.kutira.kushala.adapter.ProductAdapter
import com.kutira.kushala.databinding.FragmentSellerProductsBinding
import com.kutira.kushala.viewmodel.SellerViewModel

class ProductsFragment : Fragment() {
    private var _binding: FragmentSellerProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SellerViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SellerViewModel::class.java]

        // Vertical Grid for Seller's own product list
        adapter = ProductAdapter { product ->
            Toast.makeText(context, "Product: ${product.name}", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.loadProducts(userId)
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), AddProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.loadProducts(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}