package com.example.projectstreetkotlinver2

data class SellerProfile(
    val id: Int,
    val image: String?,
    val contact_number: String,
    val seller_description: String,
    val background_image: String?,
    val user: Int,
    var username: String? = null  // Добавляем это поле
)

