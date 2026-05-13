package com.kutira.kushala.model

data class Buyer(
    val userId: String = "",
    val companyName: String = "",
    val contactPerson: String = "",
    val businessType: String = "",
    val state: String = "",
    val district: String = "",
    val gstin: String = "",
    val savedSellers: List<String> = emptyList()
)