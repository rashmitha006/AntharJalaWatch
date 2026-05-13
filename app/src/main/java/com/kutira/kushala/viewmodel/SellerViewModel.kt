package com.kutira.kushala.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kushala.model.Product
import com.kutira.kushala.model.Seller
import com.kutira.kushala.repository.ProductRepository
import com.kutira.kushala.repository.SellerRepository
import kotlinx.coroutines.launch

class SellerViewModel(
    private val sellerRepo: SellerRepository = SellerRepository(),
    private val productRepo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _seller = MutableLiveData<Seller?>()
    val seller: LiveData<Seller?> = _seller

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    // Using a simple Boolean/String pair for status to avoid Result-in-LiveData issues
    private val _updateStatus = MutableLiveData<Pair<Boolean, String>?>()
    val updateStatus: LiveData<Pair<Boolean, String>?> = _updateStatus

    private val _addProductStatus = MutableLiveData<Pair<Boolean, String>?>()
    val addProductStatus: LiveData<Pair<Boolean, String>?> = _addProductStatus

    fun loadSellerData(sellerId: String) {
        viewModelScope.launch {
            _seller.value = sellerRepo.getSeller(sellerId)
        }
    }

    fun loadProducts(sellerId: String) {
        viewModelScope.launch {
            _products.value = productRepo.getProducts(sellerId)
        }
    }

    fun addProduct(context: Context, product: Product, imageUri: Uri? = null) {
        viewModelScope.launch {
            var finalProduct = product
            
            if (imageUri != null) {
                val base64Image = productRepo.convertImageToBase64(context, imageUri)
                if (base64Image != null) {
                    finalProduct = product.copy(photoUrls = listOf(base64Image))
                }
            }

            val result = productRepo.addProduct(finalProduct)
            if (result.isSuccess) {
                _addProductStatus.value = Pair(true, result.getOrNull() ?: "")
                loadProducts(finalProduct.sellerId)
            } else {
                _addProductStatus.value = Pair(false, result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetAddProductStatus() {
        _addProductStatus.value = null
    }

    fun updateCapacity(seller: Seller) {
        viewModelScope.launch {
            val result = sellerRepo.updateSeller(seller)
            if (result.isSuccess) {
                _updateStatus.value = Pair(true, "Success")
                _seller.value = seller
            } else {
                _updateStatus.value = Pair(false, result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }
}