package com.kutira.kushala.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kushala.model.Product
import com.kutira.kushala.utils.Constants
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ProductRepository"

    // Workaround for Spark Plan: Convert image to Base64 string to store in Firestore
    fun convertImageToBase64(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            
            // Shrink image to save space in Firestore (Max doc size 1MB)
            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val width = 400
            val height = (width / ratio).toInt().coerceAtLeast(1)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
            
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            "base64:" + Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting image", e)
            null
        }
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = db.collection(Constants.SELLERS_COLLECTION)
                .document(product.sellerId)
                .collection(Constants.PRODUCTS_COLLECTION)
                .document()
            val productWithId = product.copy(productId = docRef.id)
            docRef.set(productWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProducts(sellerId: String): List<Product> {
        return try {
            val snapshot = db.collection(Constants.SELLERS_COLLECTION)
                .document(sellerId)
                .collection(Constants.PRODUCTS_COLLECTION)
                .get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}