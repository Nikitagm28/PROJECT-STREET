package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.example.projectstreetkotlinver2.network.SellerProfile
import com.example.projectstreetkotlinver2.network.SellerProfileAdapter
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
    private lateinit var productAdapter: ProductAdapter
    private lateinit var brandLogo: ImageView
    private var sellerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_product)

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
                    fetchAllBrands()
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
            fetchAllBrands()
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
                        productAdapter = ProductAdapter(products)
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

    private fun fetchAllBrands() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sellerProfiles = fetchSellerProfiles()
                withContext(Dispatchers.Main) {
                    val brandAdapter = SellerProfileAdapter(sellerProfiles)
                    recyclerView.adapter = brandAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Toast.makeText(this@BrandsActivity, "Не удалось загрузить бренды", Toast.LENGTH_SHORT).show()
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
}
