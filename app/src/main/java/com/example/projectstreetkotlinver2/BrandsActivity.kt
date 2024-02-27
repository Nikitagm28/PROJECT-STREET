package com.example.projectstreetkotlinver2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView

class BrandsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_product)


        val favoriteButton: ImageButton = findViewById(R.id.favoriteButton)
        favoriteButton.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        // Настройка ImageView для перехода на другую активность
        val imageView: ImageView = findViewById(R.id.productImage2)
        imageView.setOnClickListener {
            val productIntent = Intent(this, ProductActivity::class.java)
            startActivity(productIntent)
        }
    }


}
