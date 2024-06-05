package com.example.projectstreetkotlinver2.network

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectstreetkotlinver2.BrandsActivity
import com.example.projectstreetkotlinver2.R

class SellerProfileAdapter(private val sellerProfiles: List<SellerProfile>) : RecyclerView.Adapter<SellerProfileAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.brandLogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sellerProfile = sellerProfiles[position]
        Glide.with(holder.itemView.context)
            .load(sellerProfile.image)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BrandsActivity::class.java)
            intent.putExtra("sellerId", sellerProfile.user)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = sellerProfiles.size
}
