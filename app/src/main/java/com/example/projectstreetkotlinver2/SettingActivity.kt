package com.example.projectstreetkotlinver2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class SettingActivity : AppCompatActivity() {

    private lateinit var settingsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Настройка BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_profile

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Ваш код для перехода на главную активность
                    true
                }

                R.id.navigation_brands -> {
                    // Ваш код для перехода на активность брендов
                    true
                }

                R.id.navigation_basket -> {
                    // Ваш код для перехода на активность корзины
                    true
                }

                R.id.navigation_profile -> {
                    // Игнорировать, если уже находимся на странице профиля
                    false
                }

                else -> false
            }
        }
    }
}
