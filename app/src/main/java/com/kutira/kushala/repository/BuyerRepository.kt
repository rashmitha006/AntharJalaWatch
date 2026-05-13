package com.kutira.kushala.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kushala.model.Inquiry
import com.kutira.kushala.utils.Constants
import kotlinx.coroutines.tasks.await

class BuyerRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun sendInquiry(inquiry: Inquiry): Result<String> {
        return try {
            val docRef = db.collection(Constants.INQUIRIES_COLLECTION).document()
            val finalInquiry = inquiry.copy(inquiryId = docRef.id)
            docRef.set(finalInquiry).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getInquiriesForSeller(sellerId: String) = 
        db.collection(Constants.INQUIRIES_COLLECTION)
            .whereEqualTo("sellerId", sellerId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)

    fun getInquiriesForBuyer(buyerId: String) = 
        db.collection(Constants.INQUIRIES_COLLECTION)
            .whereEqualTo("buyerId", buyerId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
}