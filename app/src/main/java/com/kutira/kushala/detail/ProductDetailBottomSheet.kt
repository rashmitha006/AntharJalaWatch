package com.kutira.kushala.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kushala.adapter.ImagePagerAdapter
import com.kutira.kushala.databinding.BottomSheetProductDetailBinding
import com.kutira.kushala.model.Inquiry
import com.kutira.kushala.model.Product
import com.kutira.kushala.model.User
import com.kutira.kushala.repository.BuyerRepository
import com.kutira.kushala.utils.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductDetailBottomSheet(private val product: Product) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetProductDetailBinding? = null
    private val binding get() = _binding!!
    private val buyerRepo = BuyerRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.tvBsProductName.text = product.name
        binding.tvBsPrice.text = "₹${product.pricePerUnit} / ${product.unit}"
        binding.tvBsMinQty.text = "Minimum Order: ${product.minOrderQty} ${product.unit}"
        binding.tvBsDescription.text = product.description

        if (product.photoUrls.isNotEmpty()) {
            val adapter = ImagePagerAdapter(product.photoUrls)
            binding.vpProductImages.adapter = adapter
            TabLayoutMediator(binding.tabDots, binding.vpProductImages) { _, _ -> }.attach()
        }

        binding.btnInquire.setOnClickListener {
            sendInquiry()
        }

        binding.btnDismiss.setOnClickListener { dismiss() }
    }

    private fun sendInquiry() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        binding.btnInquire.isEnabled = false
        binding.btnInquire.text = "Sending..."

        lifecycleScope.launch {
            try {
                // Fetch buyer details to include in inquiry
                val userDoc = FirebaseFirestore.getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(currentUserId)
                    .get().await()
                
                val user = userDoc.toObject(User::class.java)
                
                val inquiry = Inquiry(
                    sellerId = product.sellerId,
                    buyerId = currentUserId,
                    buyerName = user?.name ?: "Unknown Buyer",
                    buyerMobile = user?.mobile ?: "",
                    productInterest = product.name
                )

                val result = buyerRepo.sendInquiry(inquiry)
                if (result.isSuccess) {
                    Toast.makeText(context, "Inquiry sent to seller!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to send inquiry", Toast.LENGTH_SHORT).show()
                    binding.btnInquire.isEnabled = true
                    binding.btnInquire.text = "Inquire for Purchase"
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnInquire.isEnabled = true
                binding.btnInquire.text = "Inquire for Purchase"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ProductDetailBottomSheet"
    }
}