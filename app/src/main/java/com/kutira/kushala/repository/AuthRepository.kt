package com.kutira.kushala.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kushala.model.Seller
import com.kutira.kushala.model.User
import com.kutira.kushala.utils.Constants
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AuthRepository"

    fun getCurrentUser() = auth.currentUser

    suspend fun registerUser(user: User, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            val newUser = user.copy(userId = userId)
            
            // Save to users collection
            db.collection(Constants.USERS_COLLECTION).document(userId).set(newUser).await()
            Log.d(TAG, "User registered in Firestore: $userId")
            
            // If seller, initialize seller profile document so they appear in discovery
            if (user.role == Constants.ROLE_SELLER) {
                val sellerProfile = Seller(
                    userId = userId,
                    businessName = user.name,
                    village = user.village,
                    district = user.district,
                    state = user.state,
                    contactNumber = user.mobile,
                    isAvailable = true // Default to available to appear in Discover
                )
                db.collection(Constants.SELLERS_COLLECTION).document(userId).set(sellerProfile).await()
                Log.d(TAG, "Seller profile initialized for: $userId")
            }

            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            val userDoc = db.collection(Constants.USERS_COLLECTION).document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            Log.d(TAG, "User logged in: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}