package com.example.projectstreetkotlinver2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket) // Убедитесь, что здесь указан правильный layout

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_basket // Устанавливаем нужный пункт меню как выбранный

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Ваш код для перехода на главную активность
                    true
                }
                // ... обработка других пунктов меню ...
                else -> false
            }
        }

        // Здесь могут быть другие настройки для вашей активити
    }
}
