package com.kutira.kushala.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.databinding.ActivityLoginBinding
import com.kutira.kushala.seller.SellerDashboardActivity
import com.kutira.kushala.buyer.BuyerDashboardActivity
import com.kutira.kushala.utils.Constants
import com.kutira.kushala.viewmodel.AuthViewModel

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.userState.observe(this) { result ->
            result.onSuccess { user ->
                navigateToDashboard(user.role)
            }.onFailure {
                Toast.makeText(this, "Login Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = if (role == Constants.ROLE_SELLER) {
            Intent(this, SellerDashboardActivity::class.java)
        } else {
            Intent(this, BuyerDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
