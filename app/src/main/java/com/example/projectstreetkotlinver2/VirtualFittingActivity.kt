package com.example.projectstreetkotlinver2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstreetkotlinver2.adapters.ClothingAdapter
import com.example.projectstreetkotlinver2.models.ClothingItem

class VirtualFittingActivity : AppCompatActivity() {

    private lateinit var clothingRecyclerView: RecyclerView
    private lateinit var clothingAdapter: ClothingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_fitting)

        val clothingItems = listOf(
            ClothingItem(1, "Джинсы", R.drawable.ava, "Pants"),
            ClothingItem(2, "Футболка", R.drawable.ava, "Top")
            // Добавьте другие элементы одежды
        )

        clothingRecyclerView = findViewById(R.id.recycler_view_clothing)
        clothingRecyclerView.layoutManager = LinearLayoutManager(this)
        clothingAdapter = ClothingAdapter(clothingItems)
        clothingRecyclerView.adapter = clothingAdapter
    }
}
