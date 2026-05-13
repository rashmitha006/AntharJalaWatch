package com.kutira.kushala.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val role: String = "", // "SELLER" or "BUYER"
    val state: String = "",
    val district: String = "",
    val village: String = "",
    val createdAt: Long = System.currentTimeMillis()
)