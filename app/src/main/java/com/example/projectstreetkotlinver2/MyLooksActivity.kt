package com.example.projectstreetkotlinver2

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MyLooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: LookBookItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_looks)

        recyclerView = findViewById(R.id.myLooksRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Display items in a grid

        loadLookBooks()
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
}
