package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        usernameTextView = findViewById(R.id.profile_name)
        emailTextView = findViewById(R.id.profile_email)
        profileImageView = findViewById(R.id.profile_image)

        findViewById<TextView>(R.id.text_favorites).setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.text_order_lookbook).setOnClickListener {
            val intent = Intent(this, LookBookActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_profile

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
                R.id.navigation_profile -> true
                else -> false
            }
        }

        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("USERNAME", "")

        fetchUser(savedUsername)
    }

    private fun fetchUser(username: String?) {
        if (username.isNullOrEmpty()) {
            return
        }

        RetrofitClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    user?.let {
                        fetchUserProfile(it.id, it.username, it.email)
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun fetchUserProfile(userId: Int, username: String, email: StringBuffer) {
        RetrofitClient.apiService.getProfiles().enqueue(object : Callback<List<Profile>> {
            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                if (response.isSuccessful) {
                    val profiles = response.body()
                    val profile = profiles?.find { it.user == userId }
                    profile?.let {
                        updateUI(it, username, email)
                    }
                }
            }

            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun updateUI(profile: Profile, username: String, email: StringBuffer) {
        usernameTextView.text = username
        emailTextView.text = email
        profile.image?.let {
            Glide.with(this)
                .load(it)
                .into(profileImageView)
        }
    }
}
