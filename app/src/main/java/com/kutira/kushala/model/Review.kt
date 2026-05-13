package com.kutira.kushala.model

data class Review(
    val reviewId: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)