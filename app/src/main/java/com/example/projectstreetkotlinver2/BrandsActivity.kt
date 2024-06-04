package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.example.projectstreetkotlinver2.BasicActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrandsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_product) // Убедитесь, что у вас правильный макет

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Устанавливаем 2 столбца

        // Обработка нажатий элементов BottomNavigationView
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
