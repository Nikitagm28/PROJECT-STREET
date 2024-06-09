package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class BrandsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var brandLogo: ImageView
    private var sellerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brands) // Используем правильный макет

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        brandLogo = findViewById(R.id.brandLogo)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_brands

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, BasicActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_brands -> {
                    fetchAllEvents()
                    true
                }
                R.id.navigation_basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        sellerId = intent.getIntExtra("sellerId", -1)
        if (sellerId != -1) {
            fetchProductsForSeller(sellerId!!)
        } else {
            fetchAllEvents() // Fetch events if navigated through menu
        }
    }

    private fun fetchProductsForSeller(sellerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = fetchProducts().filter { it.seller == sellerId }
                val sellerProfiles = fetchSellerProfiles()
                val sellerProfile = sellerProfiles.find { it.user == sellerId }

                withContext(Dispatchers.Main) {
                    if (products.isNotEmpty()) {
                        val productAdapter = ProductAdapter(products)
                        recyclerView.adapter = productAdapter
                    } else {
                        Toast.makeText(this@BrandsActivity, "Нет товаров для данного продавца", Toast.LENGTH_SHORT).show()
                    }

                    sellerProfile?.let {
                        Glide.with(this@BrandsActivity)
                            .load(it.image)
                            .into(brandLogo)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Toast.makeText(this@BrandsActivity, "Не удалось загрузить данные", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchAllEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val events = fetchEvents()
                val users = fetchUsers()
                withContext(Dispatchers.Main) {
                    val eventAdapter = EventAdapter(events, users)
                    recyclerView.adapter = eventAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Toast.makeText(this@BrandsActivity, "Не удалось загрузить события", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun fetchProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://project-street.mooo.com/api/products/")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                val productsType = object : TypeToken<List<Product>>() {}.type
                Gson().fromJson(responseBody, productsType)
            }
        }
    }

    private suspend fun fetchSellerProfiles(): List<SellerProfile> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://project-street.mooo.com/api/seller-profiles/")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                val sellerProfilesType = object : TypeToken<List<SellerProfile>>() {}.type
                Gson().fromJson(responseBody, sellerProfilesType)
            }
        }
    }

    private suspend fun fetchEvents(): List<Event> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://project-street.mooo.com/api/events/")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                val eventsType = object : TypeToken<List<Event>>() {}.type
                Gson().fromJson(responseBody, eventsType)
            }
        }
    }

    private suspend fun fetchUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://project-street.mooo.com/api/users/")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                val usersType = object : TypeToken<List<User>>() {}.type
                Gson().fromJson(responseBody, usersType)
            }
        }
    }
}
