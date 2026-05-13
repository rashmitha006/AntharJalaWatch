package com.kutira.kushala.seller

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.kutira.kushala.BaseActivity
import com.kutira.kushala.databinding.ActivityAddProductBinding
import com.kutira.kushala.model.Product
import com.kutira.kushala.viewmodel.SellerViewModel

class AddProductActivity : BaseActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: SellerViewModel
    private var selectedImageUri: Uri? = null

    private val categories = arrayOf("Food", "Craft", "Textile", "Agarbatti", "Papad", "Basket", "Other")

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.ivPhoto1.setImageURI(selectedImageUri)
                binding.ivPhoto1.alpha = 1.0f
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SellerViewModel::class.java]

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.spinnerCategory.setAdapter(adapter)

        binding.ivPhoto1.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(600, 600)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        
        binding.ivPhoto2.visibility = android.view.View.GONE
        binding.ivPhoto3.visibility = android.view.View.GONE
        
        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        viewModel.addProductStatus.observe(this) { status ->
            status?.let { (success, message) ->
                if (success) {
                    Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.resetAddProductStatus()
                    finish()
                } else {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    viewModel.resetAddProductStatus()
                    binding.btnSaveProduct.isEnabled = true
                    binding.btnSaveProduct.text = "Save Product"
                }
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val category = binding.spinnerCategory.text.toString().trim()
        val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val unit = binding.etUnit.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (name.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSaveProduct.isEnabled = false
        binding.btnSaveProduct.text = "Saving..."

        val product = Product(
            sellerId = userId,
            name = name,
            category = category,
            description = binding.etDescription.text.toString().trim(),
            pricePerUnit = price,
            unit = unit,
            minOrderQty = binding.etMinQty.text.toString().toIntOrNull() ?: 1,
            productionDays = binding.etProdTime.text.toString().toIntOrNull() ?: 1
        )
        
        viewModel.addProduct(applicationContext, product, selectedImageUri)
    }
}
