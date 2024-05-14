package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrandsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_product)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Устанавливаем 2 столбца

        // Обработка нажатий элементов BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_brands -> true
                R.id.navigation_basket -> {
                    val basketIntent = Intent(this, BasketActivity::class.java)
                    startActivity(basketIntent)
                    true
                }
                R.id.navigation_profile -> {
                    val settingsIntent = Intent(this, SettingActivity::class.java)
                    startActivity(settingsIntent)
                    true
                }
                else -> false
            }
        }

        fetchProducts()
    }

    private fun fetchProducts() {
        val apiService = RetrofitClient.apiService
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productAdapter = ProductAdapter(products)
                        recyclerView.adapter = productAdapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
