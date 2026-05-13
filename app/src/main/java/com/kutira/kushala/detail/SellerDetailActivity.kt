package com.kutira.kushala.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.adapter.ProductAdapter
import com.kutira.kushala.databinding.ActivitySellerDetailBinding
import com.kutira.kushala.model.Seller
import com.kutira.kushala.viewmodel.BuyerViewModel
import com.kutira.kushala.viewmodel.SellerViewModel

class SellerDetailActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerDetailBinding
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var buyerViewModel: BuyerViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sellerId = intent.getStringExtra("SELLER_ID") ?: return finish()

        sellerViewModel = ViewModelProvider(this)[SellerViewModel::class.java]
        buyerViewModel = ViewModelProvider(this)[BuyerViewModel::class.java]

        setupRecyclerView()
        
        sellerViewModel.loadSellerData(sellerId)
        sellerViewModel.loadProducts(sellerId)
        buyerViewModel.incrementViewCount(sellerId)

        sellerViewModel.seller.observe(this) { seller ->
            seller?.let { updateUI(it) }
        }

        sellerViewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            binding.tvDetailProductsCount.text = "📦 ${products.size} products"
        }

        binding.btnCallNow.setOnClickListener {
            val seller = sellerViewModel.seller.value
            if (seller != null && seller.contactNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${seller.contactNumber}")
                startActivity(intent)
            }
        }

        binding.btnWhatsApp.setOnClickListener {
            val seller = sellerViewModel.seller.value
            if (seller != null && seller.contactNumber.isNotEmpty()) {
                val message = "Hello, I found your business on Kutira-Kushala. I am interested in your products. Please share details."
                val url = "https://api.whatsapp.com/send?phone=91${seller.contactNumber}&text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }
        
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val bottomSheet = ProductDetailBottomSheet(product)
            bottomSheet.show(supportFragmentManager, ProductDetailBottomSheet.TAG)
        }
        binding.rvDetailProducts.adapter = productAdapter
    }

    private fun updateUI(seller: Seller) {
        binding.tvDetailBusinessName.text = seller.businessName
        binding.tvDetailSkillArea.text = seller.skillArea
        binding.tvDetailLocation.text = "${seller.village}, ${seller.district}, ${seller.state}"
        binding.tvDetailRating.text = "⭐ ${seller.avgRating}"
        binding.tvDetailViews.text = "👁 ${seller.profileViews} views"
        binding.tvDetailDescription.text = seller.description
        
        if (seller.isAvailable) {
            binding.tvDetailCapacityStatus.text = "🟢 Available: ${seller.unitsAvailableThisWeek} units this week"
            binding.tvDetailCapacityStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            binding.tvDetailCapacityStatus.text = "🔴 Not Taking Orders"
            binding.tvDetailCapacityStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
        
        binding.tvDetailLeadTime.text = "Lead time: ${seller.leadTimeDays} days"
        binding.tvDetailAdvance.text = "Accepts Advance Booking: ${if (seller.acceptsAdvance) "Yes" else "No"}"

        if (seller.teamPhotoUrl.isNotEmpty()) {
            Glide.with(this).load(seller.teamPhotoUrl).into(binding.ivDetailTeamPhoto)
        }
    }
}
