package com.example.projectstreetkotlinver2.models

data class ClothingItem(
    val id: Int,
    val name: String,
    val imageResource: Int,  // ID ресурса изображения
    val category: String
)
