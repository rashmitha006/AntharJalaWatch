package com.kutira.kushala.seller

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.R
import com.kutira.kushala.databinding.ActivitySellerDashboardBinding

class SellerDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_seller)
        binding.bottomNavSeller.setupWithNavController(navController)
    }
}
