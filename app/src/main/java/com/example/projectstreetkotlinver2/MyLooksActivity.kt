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
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class MyLooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: LookBookItemAdapter
    private lateinit var overlayContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_looks)

        recyclerView = findViewById(R.id.myLooksRecyclerView)
        overlayContainer = findViewById(R.id.overlay_container)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Display items in a grid

        loadLookBooks()

        // Настройка BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_create)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_looks -> {
                    // текущий экран
                    true
                }
                R.id.navigation_create -> {
                    startActivity(Intent(this, LookBookActivity::class.java))
                    true
                }
                R.id.navigation_feed -> {
                    // переход на экран ленты
                    true
                }
                R.id.navigation_favorites -> {
                    // переход на экран избранного
                    true
                }
                else -> false
            }
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
                    Toast.makeText(this, "Error loading LookBooks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
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
                ) // увеличьте или уменьшите значение 300 по необходимости
                layoutParams.topMargin = 8 // уменьшите значение для уменьшения расстояния между элементами
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
            // код для кнопки "Поделиться"
        }

        deleteButton.setOnClickListener {
            deleteLookBook(lookBookItems)
        }

        overlayContainer.addView(overlayView)

        overlayContainer.setOnClickListener {
            overlayContainer.visibility = View.GONE
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
                                        Toast.makeText(this, "LookBook deleted successfully", Toast.LENGTH_SHORT).show()
                                        loadLookBooks()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error deleting LookBook: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                break
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error finding LookBook: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
