package com.example.projectstreetkotlinver2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class LookBookItemAdapter(private val lookBooks: List<List<LookBookItem>>) :
    RecyclerView.Adapter<LookBookItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lookBookContainer: LinearLayout = view.findViewById(R.id.lookBookContainer)
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
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300) // увеличьте или уменьшите значение 300 по необходимости
            layoutParams.topMargin = 8 // уменьшите значение для уменьшения расстояния между элементами
            imageView.layoutParams = layoutParams
            Picasso.get().load(item.imageUrl).into(imageView)
            holder.lookBookContainer.addView(imageView)
        }
    }

    override fun getItemCount(): Int = lookBooks.size
}
