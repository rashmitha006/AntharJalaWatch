package com.kutira.kushala.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.R
import com.kutira.kushala.databinding.ActivityRegisterBinding
import com.kutira.kushala.model.User
import com.kutira.kushala.seller.SellerDashboardActivity
import com.kutira.kushala.buyer.BuyerDashboardActivity
import com.kutira.kushala.utils.Constants
import com.kutira.kushala.viewmodel.AuthViewModel

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    private val indianStates = arrayOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
        "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
        "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
        "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, indianStates)
        binding.spinnerState.setAdapter(adapter)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val mobile = binding.etMobile.text.toString()
            val password = binding.etPassword.text.toString()
            val state = binding.spinnerState.text.toString()
            val district = binding.etDistrict.text.toString()
            val village = binding.etVillage.text.toString()
            
            val role = if (binding.toggleRole.checkedButtonId == R.id.btnRoleSeller) {
                Constants.ROLE_SELLER
            } else {
                Constants.ROLE_BUYER
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && mobile.isNotEmpty()) {
                val user = User(
                    name = name,
                    email = email,
                    mobile = mobile,
                    role = role,
                    state = state,
                    district = district,
                    village = village
                )
                viewModel.register(user, password)
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        viewModel.userState.observe(this) { result ->
            result.onSuccess { user ->
                navigateToDashboard(user.role)
            }.onFailure {
                Toast.makeText(this, "Registration Failed: ${it.message}", Toast.LENGTH_SHORT).show()
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
