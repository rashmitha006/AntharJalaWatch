package com.kutira.kushala.buyer

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.R
import com.kutira.kushala.databinding.ActivityBuyerDashboardBinding

class BuyerDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_buyer) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavBuyer.setupWithNavController(navController)
    }
}
