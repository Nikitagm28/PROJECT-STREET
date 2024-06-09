package com.example.projectstreetkotlinver2.network

import com.example.projectstreetkotlinver2.Product
import com.example.projectstreetkotlinver2.Profile
import com.example.projectstreetkotlinver2.SellerProfile
import com.example.projectstreetkotlinver2.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("products/seller/{sellerId}/")
    fun getProductsBySeller(@Path("sellerId") sellerId: Int): Call<List<Product>>

    @POST("register/?format=api")
    suspend fun registerUser(@Body request: UserRegisterRequest): Response<UserRegisterResponse>
}