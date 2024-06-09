package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.squareup.picasso.Picasso

class MyLooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: LookBookItemAdapter
    private lateinit var overlayContainer: FrameLayout
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_looks)

        recyclerView = findViewById(R.id.myLooksRecyclerView)
        overlayContainer = findViewById(R.id.overlay_container)
        usernameTextView = findViewById(R.id.username_text)
        profileImageView = findViewById(R.id.profile_image)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Отображение элементов в виде сетки

        loadUserProfile()
        loadLookBooks()

        // Настройка BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_create)
        bottomNavigationView.selectedItemId = R.id.navigation_my_looks
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_looks -> {
                    // текущий экран
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

    private fun loadLookBooks() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            db.collection("lookbooks")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { result ->
                    val lookBooks = result.toLookBookItems()
                    adapter = LookBookItemAdapter(lookBooks)
                    recyclerView.adapter = adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка загрузки образов: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Имя пользователя не найдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun QuerySnapshot.toLookBookItems(): List<List<LookBookItem>> {
        val lookBookItemsList = mutableListOf<List<LookBookItem>>()
        for (document in this) {
            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
            val lookBookItems = mutableListOf<LookBookItem>()
            for (item in items) {
                val imageUrl = item["imageUrl"] as? String ?: ""
                val x = (item["x"] as? Double)?.toFloat() ?: 0f
                val y = (item["y"] as? Double)?.toFloat() ?: 0f
                lookBookItems.add(LookBookItem(imageUrl, x, y))
            }
            lookBookItemsList.add(lookBookItems)
        }
        return lookBookItemsList
    }

    inner class LookBookItemAdapter(private val lookBooks: List<List<LookBookItem>>) :
        RecyclerView.Adapter<LookBookItemAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val lookBookContainer: LinearLayout = view.findViewById(R.id.lookBookContainer)

            init {
                view.setOnLongClickListener {
                    showOverlay(lookBooks[adapterPosition])
                    true
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lookbook, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lookBookItems = lookBooks[position]
            holder.lookBookContainer.removeAllViews()
            for (item in lookBookItems) {
                val imageView = ImageView(holder.lookBookContainer.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    300
                )
                layoutParams.topMargin = 8
                imageView.layoutParams = layoutParams
                Picasso.get().load(item.imageUrl).into(imageView)
                holder.lookBookContainer.addView(imageView)
            }
        }

        override fun getItemCount(): Int = lookBooks.size
    }

    private fun showOverlay(lookBookItems: List<LookBookItem>) {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE

        val overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, overlayContainer, false)
        val overlayImage: LinearLayout = overlayView.findViewById(R.id.overlay_image)
        val shareButton: Button = overlayView.findViewById(R.id.share_button)
        val deleteButton: Button = overlayView.findViewById(R.id.delete_button)

        for (item in lookBookItems) {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            )
            layoutParams.topMargin = 8
            imageView.layoutParams = layoutParams
            Picasso.get().load(item.imageUrl).into(imageView)
            overlayImage.addView(imageView)
        }

        shareButton.setOnClickListener {
            shareLookBook(lookBookItems)
        }

        deleteButton.setOnClickListener {
            deleteLookBook(lookBookItems)
        }

        overlayContainer.addView(overlayView)

        overlayContainer.setOnClickListener {
            overlayContainer.visibility = View.GONE
        }
    }

    private fun shareLookBook(lookBookItems: List<LookBookItem>) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            val lookbookData = hashMapOf(
                "username" to username,
                "lookBookItems" to lookBookItems.map { item ->
                    mapOf(
                        "imageUrl" to item.imageUrl,
                        "x" to item.x,
                        "y" to item.y
                    )
                },
                "likeCount" to 0
            )

            db.collection("feed_lookbooks")
                .add(lookbookData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Образ успешно добавлен в ленту!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при добавлении образа в ленту: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Имя пользователя не найдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteLookBook(lookBookItems: List<LookBookItem>) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)

        if (username != null) {
            db.collection("lookbooks")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val items = document.get("items") as? List<Map<String, Any>>
                        if (items != null && items.size == lookBookItems.size) {
                            var match = true
                            for (i in items.indices) {
                                val item = items[i]
                                val lookBookItem = lookBookItems[i]
                                if (item["imageUrl"] != lookBookItem.imageUrl ||
                                    item["x"] != lookBookItem.x.toDouble() ||
                                    item["y"] != lookBookItem.y.toDouble()) {
                                    match = false
                                    break
                                }
                            }
                            if (match) {
                                db.collection("lookbooks").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Образ успешно удален", Toast.LENGTH_SHORT).show()
                                        loadLookBooks()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Ошибка при удалении образа: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                break
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при поиске образа: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
