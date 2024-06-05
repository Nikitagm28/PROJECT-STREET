package com.example.projectstreetkotlinver2.network

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class User(
    val id: Int,
    val username: String,
    val email: String
)
data class Profile(
    val id: Int,
    val image: String?,
    val user: Int
)

data class SellerProfile(
    val id: Int,
    val image: String?,
    val user: Int
)
