package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FeedActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: FeedAdapter
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        recyclerView = findViewById(R.id.feedRecyclerView)
        usernameTextView = findViewById(R.id.username_text)
        profileImageView = findViewById(R.id.profile_image)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        loadUserProfile()
        loadFeedItems()

        // Настройка BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_feed)
        bottomNavigationView.selectedItemId = R.id.navigation_feed
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
                    // текущий экран
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, SavedLooksActivity::class.java))
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

    private fun loadFeedItems() {
        db.collection("feed_lookbooks")
            .get()
            .addOnSuccessListener { result ->
                val feedItems = result.toFeedLookBookItems()
                adapter = FeedAdapter(feedItems) { item -> saveLookBookItem(item) }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка загрузки ленты: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveLookBookItem(item: FeedLookBookItem) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("USERNAME", "") ?: return

        // Проверяем, есть ли уже сохраненный образ с таким же ID
        db.collection("saved_lookbooks")
            .whereEqualTo("username", item.username)
            .whereEqualTo("savedBy", savedUsername)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val savedItem = hashMapOf(
                        "username" to item.username,
                        "userProfileImage" to item.userProfileImage,
                        "lookBookItems" to item.lookBookItems.map {
                            mapOf(
                                "imageUrl" to it.imageUrl,
                                "x" to it.x,
                                "y" to it.y
                            )
                        },
                        "savedBy" to savedUsername
                    )

                    db.collection("saved_lookbooks")
                        .add(savedItem)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Образ сохранен", Toast.LENGTH_SHORT).show()
                            Log.d("FeedActivity", "Образ сохранен с userProfileImage: ${item.userProfileImage}")
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Ошибка при сохранении образа: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("FeedActivity", "Ошибка при сохранении образа: ${e.message}")
                        }
                } else {
                    Toast.makeText(this, "Этот образ уже сохранен", Toast.LENGTH_SHORT).show()
                    Log.d("FeedActivity", "Этот образ уже сохранен")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при проверке сохраненного образа: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FeedActivity", "Ошибка при проверке сохраненного образа: ${e.message}")
            }
    }

    private fun QuerySnapshot.toFeedLookBookItems(): List<FeedLookBookItem> {
        val feedItems = mutableListOf<FeedLookBookItem>()
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
            val likeCount = document.getLong("likeCount")?.toInt() ?: 0
            val likedBy = document.get("likedBy") as? List<String> ?: emptyList()
            feedItems.add(FeedLookBookItem(document.id, username, userProfileImage, lookBookItems, likeCount, likedBy.toMutableList()))
        }
        return feedItems
    }
}
