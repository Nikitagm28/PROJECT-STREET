package com.example.projectstreetkotlinver2.network

import com.example.projectstreetkotlinver2.Product
import com.example.projectstreetkotlinver2.Profile
import com.example.projectstreetkotlinver2.SellerProfile
import com.example.projectstreetkotlinver2.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/products/")
    fun getProducts(): Call<List<Product>>

    @POST("token/")
    fun getToken(@Body request: UserLoginRequest): Call<UserLoginResponse>

    @GET("users/")
    fun getUsers(): Call<List<User>>

    @GET("profiles/")
    fun getProfiles(): Call<List<Profile>>

    @GET("seller-profiles/")
    fun getSellerProfiles(): Call<List<SellerProfile>>

}