package com.example.projectstreetkotlinver2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket) // Используйте layout файл, который соответствует вашему дизайну страницы корзины
    }
}
