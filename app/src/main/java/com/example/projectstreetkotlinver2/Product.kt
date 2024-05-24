package com.example.projectstreetkotlinver2

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val description: String,
    val image: String,
    val quantity_available: Int,
    val seller: Int,
    val category: Int,
    val sizes: List<String>,
    var selectedSize: String? = null,
    var isSelected: Boolean = false
) : Serializable
