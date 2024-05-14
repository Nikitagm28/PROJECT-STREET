package com.example.projectstreetkotlinver2.network

import com.example.projectstreetkotlinver2.Product
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/products/")
    fun getProducts(): Call<List<Product>>
}
