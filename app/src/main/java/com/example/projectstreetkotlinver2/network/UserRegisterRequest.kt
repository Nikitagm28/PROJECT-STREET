package com.example.projectstreetkotlinver2.network

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
