package com.example.projectstreetkotlinver2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        sharedPreferences = parent.context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_recycler, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        Picasso.get().load(product.image).into(holder.productImage)
        holder.productName.text = product.name
        holder.productPrice.text = "${product.price} Р"

        holder.favoriteButton.isSelected = isFavorite(product)

        holder.favoriteButton.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                addFavorite(product)
            } else {
                removeFavorite(product)
            }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductActivity::class.java).apply {
                putExtra("product", product)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun isFavorite(product: Product): Boolean {
        val favorites = getFavorites()
        return favorites.contains(product.id)
    }

    private fun addFavorite(product: Product) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(product.id)
        saveFavorites(favorites)
    }

    private fun removeFavorite(product: Product) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(product.id)
        saveFavorites(favorites)
    }

    private fun getFavorites(): Set<Int> {
        val json = sharedPreferences.getString("favorites", null)
        return if (json != null) {
            val type = object : TypeToken<Set<Int>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptySet()
        }
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val json = Gson().toJson(favorites)
        sharedPreferences.edit().putString("favorites", json).apply()
    }
}