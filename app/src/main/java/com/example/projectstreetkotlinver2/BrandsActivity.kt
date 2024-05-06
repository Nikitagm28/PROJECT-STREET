package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BrandsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_product)

        // Кнопка добавления в избранное
        val favoriteButton: ImageButton = findViewById(R.id.favoriteButton)
        favoriteButton.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        // Настройка ImageView для перехода на страницу продукта
        val imageView: ImageView = findViewById(R.id.productImage2)
        imageView.setOnClickListener {
            val productIntent = Intent(this, ProductActivity::class.java)
            startActivity(productIntent)
        }

        // Обработка нажатий элементов BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation) // Убедитесь, что у вас есть элемент с идентификатором bottom_navigation в вашем layout
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Код для перехода на главную активность
                    true
                }
                R.id.navigation_brands -> {
                    // Код для перехода на активность брендов
                    true
                }
                R.id.navigation_basket -> {
                    // Переход на активность корзины
                    val basketIntent = Intent(this, BasketActivity::class.java)
                    startActivity(basketIntent)
                    true
                }
                R.id.navigation_profile -> {
                    // Код для перехода на активность профиля
                    val settingsIntent = Intent(this, SettingActivity::class.java)
                    startActivity(settingsIntent)
                    true
                }
                else -> false
            }
        }
    }
}
