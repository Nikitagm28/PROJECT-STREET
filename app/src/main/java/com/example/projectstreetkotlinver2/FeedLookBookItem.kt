package com.example.projectstreetkotlinver2

data class FeedLookBookItem(
    val documentId: String,
    val username: String,
    val userProfileImage: String?,
    val lookBookItems: List<LookBookItem>,
    var likeCount: Int = 0,
    val likedBy: MutableList<String> = mutableListOf()
)
