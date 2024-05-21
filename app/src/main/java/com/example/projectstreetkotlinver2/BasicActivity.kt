package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BasicActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewNewArrivals)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Сетка с двумя столбцами

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_brands -> {
                    val intent = Intent(this, BrandsActivity::class.java)
                    startActivity(intent)
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

        // Добавление обработчика клика для YUMMS
        val yummsImageView: ImageView = findViewById(R.id.yumms)
        yummsImageView.setOnClickListener {
            val intent = Intent(this, BrandsActivity::class.java)
            startActivity(intent)
        }

        fetchProducts()
    }

    private fun fetchProducts() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://51.250.54.133:8000/api/products/") // Замените на актуальный URL API
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@BasicActivity, "Не удалось загрузить товары", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val productsType = object : TypeToken<List<Product>>() {}.type
                    val products: List<Product> = Gson().fromJson(responseBody, productsType)
                    runOnUiThread {
                        val adapter = ProductAdapter(products)
                        recyclerView.adapter = adapter
                    }
                }
            }
        })
    }
}
