package com.kutira.kushala.model

data class Product(
    val productId: String = "",
    val sellerId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val pricePerUnit: Double = 0.0,
    val minOrderQty: Int = 0,
    val unit: String = "",
    val productionDays: Int = 0,
    val photoUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)