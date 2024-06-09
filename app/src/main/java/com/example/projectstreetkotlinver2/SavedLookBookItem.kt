package com.example.projectstreetkotlinver2

data class SavedLookBookItem(
    val username: String,
    val userProfileImage: String?,
    val lookBookItems: List<LookBookItem>
)
