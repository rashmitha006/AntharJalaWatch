package com.kutira.kushala

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kushala.auth.LoginActivity
import com.kutira.kushala.buyer.BuyerDashboardActivity
import com.kutira.kushala.seller.SellerDashboardActivity
import com.kutira.kushala.utils.Constants

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == Constants.ROLE_SELLER) {
                        startActivity(Intent(this, SellerDashboardActivity::class.java))
                    } else if (role == Constants.ROLE_BUYER) {
                        startActivity(Intent(this, BuyerDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }
}
