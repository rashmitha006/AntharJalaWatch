package com.kutira.kushala.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kushala.model.Seller
import com.kutira.kushala.repository.SellerRepository
import kotlinx.coroutines.launch

class BuyerViewModel(private val sellerRepo: SellerRepository = SellerRepository()) : ViewModel() {

    private val _sellers = MutableLiveData<List<Seller>>()
    val sellers: LiveData<List<Seller>> = _sellers

    private val TAG = "BuyerViewModel"

    fun loadAllSellers() {
        Log.d(TAG, "Starting to load all sellers")
        viewModelScope.launch {
            sellerRepo.getAllSellers().addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error listening for sellers", e)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Seller::class.java) ?: emptyList()
                Log.d(TAG, "Fetched ${list.size} sellers")
                _sellers.value = list
            }
        }
    }

    fun incrementViewCount(sellerId: String) {
        viewModelScope.launch {
            sellerRepo.incrementProfileViews(sellerId)
        }
    }
}