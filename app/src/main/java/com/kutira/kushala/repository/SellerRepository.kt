package com.kutira.kushala.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kutira.kushala.model.Seller
import com.kutira.kushala.model.User
import com.kutira.kushala.utils.Constants
import kotlinx.coroutines.tasks.await

class SellerRepository {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "SellerRepository"

    suspend fun getSeller(sellerId: String): Seller? {
        return try {
            Log.d(TAG, "Fetching seller data for: $sellerId")
            val doc = db.collection(Constants.SELLERS_COLLECTION).document(sellerId).get().await()
            var seller = doc.toObject(Seller::class.java)
            
            if (seller == null) {
                Log.w(TAG, "Seller document not found for: $sellerId. Attempting to create from user data.")
                seller = createProfileFromUser(sellerId)
            }
            seller
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching seller: $sellerId", e)
            null
        }
    }

    private suspend fun createProfileFromUser(userId: String): Seller? {
        return try {
            val userDoc = db.collection(Constants.USERS_COLLECTION).document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            if (user != null && user.role == Constants.ROLE_SELLER) {
                val sellerProfile = Seller(
                    userId = userId,
                    businessName = user.name,
                    village = user.village,
                    district = user.district,
                    state = user.state,
                    contactNumber = user.mobile,
                    isAvailable = true
                )
                db.collection(Constants.SELLERS_COLLECTION).document(userId).set(sellerProfile).await()
                Log.d(TAG, "Created missing seller profile for: $userId")
                sellerProfile
            } else {
                Log.e(TAG, "Could not create seller profile: User not found or not a seller")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating profile from user: $userId", e)
            null
        }
    }

    suspend fun updateSeller(seller: Seller): Result<Unit> {
        return try {
            Log.d(TAG, "Updating seller data for: ${seller.userId}")
            db.collection(Constants.SELLERS_COLLECTION).document(seller.userId)
                .set(seller, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating seller: ${seller.userId}", e)
            Result.failure(e)
        }
    }

    suspend fun incrementProfileViews(sellerId: String) {
        try {
            Log.d(TAG, "Incrementing views for: $sellerId")
            val docRef = db.collection(Constants.SELLERS_COLLECTION).document(sellerId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val newViews = (snapshot.getLong("profileViews") ?: 0) + 1
                    transaction.update(docRef, "profileViews", newViews)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing views for: $sellerId", e)
        }
    }

    fun getAllSellers() = db.collection(Constants.SELLERS_COLLECTION)
}