package com.example.projectstreetkotlinver2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class VirtualFittingActivity : AppCompatActivity() {

    private lateinit var lookBookRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_fitting)

        lookBookRecyclerView = findViewById(R.id.recycler_view_clothing)

        val lookBookItems = getLookBookItems()
        lookBookRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lookBookRecyclerView.adapter = LookBookAdapter(lookBookItems)
    }

    private fun getLookBookItems(): List<Product> {
        val sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("lookbook_items", null)
        return if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    inner class LookBookAdapter(private val items: List<Product>) : RecyclerView.Adapter<LookBookAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.lookBookItemImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.lookbook_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            Picasso.get().load(product.image).into(holder.imageView)
        }

        override fun getItemCount(): Int = items.size
    }
}
