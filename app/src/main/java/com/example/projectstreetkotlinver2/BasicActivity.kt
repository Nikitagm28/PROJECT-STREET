package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.example.projectstreetkotlinver2.network.SellerProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class BasicActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var brandsLayout: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var products: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewNewArrivals)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        usernameTextView = findViewById(R.id.username_text)
        profileImageView = findViewById(R.id.profile_image)
        brandsLayout = findViewById(R.id.brands_layout)
        searchEditText = findViewById(R.id.search_edit_text)

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

        val yummsImageView: ImageView = findViewById(R.id.yumms)
        yummsImageView.setOnClickListener {
            val intent = Intent(this, BrandsActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("USERNAME", "")

        fetchUser(savedUsername)
        fetchProducts()
        fetchNewBrands()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchUser(username: String?) {
        if (username.isNullOrEmpty()) {
            return
        }

        RetrofitClient.apiService.getUsers().enqueue(object : retrofit2.Callback<List<User>> {
            override fun onResponse(call: retrofit2.Call<List<User>>, response: retrofit2.Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    user?.let {
                        fetchUserProfile(it.id, it.username)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<User>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun fetchUserProfile(userId: Int, username: String) {
        RetrofitClient.apiService.getProfiles().enqueue(object : retrofit2.Callback<List<Profile>> {
            override fun onResponse(call: retrofit2.Call<List<Profile>>, response: retrofit2.Response<List<Profile>>) {
                if (response.isSuccessful) {
                    val profiles = response.body()
                    val profile = profiles?.find { it.user == userId }
                    profile?.let {
                        updateUI(it, username)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Profile>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun updateUI(profile: Profile, username: String) {
        usernameTextView.text = username
        profile.image?.let {
            Glide.with(this)
                .load(it)
                .into(profileImageView)
        }
    }

    private fun fetchProducts() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://project-street.mooo.com/api/products/")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@BasicActivity, "Не удалось загрузить товары", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val productsType = object : TypeToken<List<Product>>() {}.type
                    products = Gson().fromJson(responseBody, productsType)
                    runOnUiThread {
                        val adapter = ProductAdapter(products)
                        recyclerView.adapter = adapter
                    }
                }
            }
        })
    }

    private fun fetchNewBrands() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://project-street.mooo.com/api/seller-profiles/")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@BasicActivity, "Не удалось загрузить бренды", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val sellerProfilesType = object : TypeToken<List<SellerProfile>>() {}.type
                    val sellerProfiles: List<SellerProfile> = Gson().fromJson(responseBody, sellerProfilesType)
                    runOnUiThread {
                        updateNewBrands(sellerProfiles)
                    }
                }
            }
        })
    }

    private fun updateNewBrands(sellerProfiles: List<SellerProfile>) {
        brandsLayout.removeAllViews()
        sellerProfiles.forEach { sellerProfile ->
            val imageView = ImageView(this)
            imageView.layoutParams = LinearLayout.LayoutParams(480, 400).apply {
                setMargins(8, 8, 8, 8)
            }
            sellerProfile.image?.let {
                Glide.with(this)
                    .load(it)
                    .into(imageView)
            }
            imageView.setOnClickListener {
                val intent = Intent(this, BrandsActivity::class.java)
                intent.putExtra("sellerId", sellerProfile.user)
                startActivity(intent)
            }
            brandsLayout.addView(imageView)
        }
    }

    private fun filterProducts(query: String) {
        val filteredProducts = products.filter { it.name.contains(query, ignoreCase = true) }
        val adapter = ProductAdapter(filteredProducts)
        recyclerView.adapter = adapter
    }
}
