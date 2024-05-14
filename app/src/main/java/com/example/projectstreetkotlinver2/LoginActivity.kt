package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Находим кнопку по ID
        val enterButton = findViewById<Button>(R.id.login_button)

        // Устанавливаем слушатель нажатия на кнопку
        enterButton.setOnClickListener {
            // Создаем намерение для запуска LoginActivity
            val brandsIntent = Intent(this, BrandsActivity::class.java)
            // Запускаем LoginActivity
            startActivity(brandsIntent)
        }
    }
}