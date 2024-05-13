package com.example.projectstreetkotlinver2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstreetkotlinver2.R
import com.example.projectstreetkotlinver2.models.ClothingItem

class ClothingAdapter(private val clothingList: List<ClothingItem>) : RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder>() {

    class ClothingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.clothing_image)
        val textView: TextView = itemView.findViewById(R.id.clothing_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_clothing, parent, false)
        return ClothingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClothingViewHolder, position: Int) {
        val currentItem = clothingList[position]
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.name
    }

    override fun getItemCount() = clothingList.size
}
