package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SavedLooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: SavedLooksAdapter
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_looks)

        recyclerView = findViewById(R.id.savedLooksRecyclerView)
        usernameTextView = findViewById(R.id.username_text)
        profileImageView = findViewById(R.id.profile_image)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        loadUserProfile()
        loadSavedLooks()

        // Настройка BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_saved)
        bottomNavigationView.selectedItemId = R.id.navigation_favorites
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_looks -> {
                    startActivity(Intent(this, MyLooksActivity::class.java))
                    true
                }
                R.id.navigation_back -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                R.id.navigation_create -> {
                    startActivity(Intent(this, LookBookActivity::class.java))
                    true
                }
                R.id.navigation_feed -> {
                    startActivity(Intent(this, FeedActivity::class.java))
                    true
                }
                R.id.navigation_favorites -> {
                    // текущий экран
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("USERNAME", "")

        if (!savedUsername.isNullOrEmpty()) {
            RetrofitClient.apiService.getUsers().enqueue(object : retrofit2.Callback<List<User>> {
                override fun onResponse(call: retrofit2.Call<List<User>>, response: retrofit2.Response<List<User>>) {
                    if (response.isSuccessful) {
                        val users = response.body()
                        val user = users?.find { it.username == savedUsername }
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

    private fun loadSavedLooks() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            db.collection("saved_lookbooks")
                .whereEqualTo("savedBy", username)
                .get()
                .addOnSuccessListener { result ->
                    val savedLooks = result.toSavedLookBookItems()
                    adapter = SavedLooksAdapter(this, savedLooks) { lookBookItem ->
                        deleteLook(lookBookItem)
                    }
                    recyclerView.adapter = adapter
                }
                .addOnFailureListener { e ->
                    // Логируем ошибку для отладки
                    Log.e("SavedLooksActivity", "Ошибка загрузки сохраненных образов: ${e.message}")
                }
        }
    }

    private fun deleteLook(lookBookItem: SavedLookBookItem) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            db.collection("saved_lookbooks")
                .whereEqualTo("savedBy", username)
                .whereEqualTo("username", lookBookItem.username)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                runOnUiThread {
                                    adapter.removeItem(lookBookItem)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("SavedLooksActivity", "Ошибка удаления образа: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("SavedLooksActivity", "Ошибка удаления образа: ${e.message}")
                }
        }
    }

    private fun QuerySnapshot.toSavedLookBookItems(): List<SavedLookBookItem> {
        val savedItems = mutableListOf<SavedLookBookItem>()
        for (document in this) {
            val username = document.getString("username") ?: ""
            val userProfileImage = document.getString("userProfileImage")
            val items = document.get("lookBookItems") as? List<Map<String, Any>> ?: emptyList()
            val lookBookItems = mutableListOf<LookBookItem>()
            for (item in items) {
                val imageUrl = item["imageUrl"] as? String ?: ""
                val x = (item["x"] as? Double)?.toFloat() ?: 0f
                val y = (item["y"] as? Double)?.toFloat() ?: 0f
                lookBookItems.add(LookBookItem(imageUrl, x, y))
            }
            savedItems.add(SavedLookBookItem(username, userProfileImage, lookBookItems))
        }
        return savedItems
    }
}
