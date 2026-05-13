package com.kutira.kushala.model

data class Inquiry(
    val inquiryId: String = "",
    val sellerId: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerMobile: String = "",
    val productInterest: String = "",
    val timestamp: Long = System.currentTimeMillis()
)