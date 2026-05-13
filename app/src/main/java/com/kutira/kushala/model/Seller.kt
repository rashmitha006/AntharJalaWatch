package com.kutira.kushala.model

data class Seller(
    val userId: String = "",
    val businessName: String = "",
    val skillArea: String = "",
    val description: String = "",
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val teamPhotoUrl: String = "",
    val yearsExperience: Int = 0,
    val languages: List<String> = emptyList(),
    val certifications: String = "",
    val contactNumber: String = "",
    val showContact: Boolean = true,
    @field:JvmField
    val isAvailable: Boolean = false,
    val dailyCapacity: Int = 0,
    val unitsAvailableThisWeek: Int = 0,
    val leadTimeDays: Int = 0,
    val acceptsAdvance: Boolean = false,
    val activeMonths: List<Int> = emptyList(), // 1 to 12
    val productCategories: List<String> = emptyList(), // Added to help discovery
    val profileViews: Int = 0,
    val avgRating: Double = 0.0,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)