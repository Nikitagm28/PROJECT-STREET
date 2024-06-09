package com.example.projectstreetkotlinver2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class SavedLooksAdapter(
    private val context: Context,
    private var savedItems: List<SavedLookBookItem>,
    private val onDeleteClick: (SavedLookBookItem) -> Unit
) : RecyclerView.Adapter<SavedLooksAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val profileName: TextView = view.findViewById(R.id.profile_name)
        val lookBookContainer: LinearLayout = view.findViewById(R.id.lookBookContainer)
        val deleteIcon: ImageView = view.findViewById(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_look_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val savedItem = savedItems[position]
        holder.profileName.text = savedItem.username

        // Логируем URL изображения профиля
        Log.d("SavedLooksAdapter", "userProfileImage URL: ${savedItem.userProfileImage}")

        if (savedItem.userProfileImage != null) {
            Glide.with(holder.itemView.context)
                .load(savedItem.userProfileImage)
                .into(holder.profileImage)
        } else {
            Log.w("SavedLooksAdapter", "userProfileImage is null")
        }

        holder.lookBookContainer.removeAllViews()
        for (item in savedItem.lookBookItems) {
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

        holder.deleteIcon.setOnClickListener {
            onDeleteClick(savedItem)
        }
    }

    override fun getItemCount(): Int = savedItems.size

    fun removeItem(lookBookItem: SavedLookBookItem) {
        val updatedItems = savedItems.toMutableList()
        updatedItems.remove(lookBookItem)
        savedItems = updatedItems
        notifyDataSetChanged()
    }
}