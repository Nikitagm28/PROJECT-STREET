package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 2000 // Задержка в миллисекундах (2000 мс = 2 секунды)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Найти ImageView по ID
        val logoImageView = findViewById<ImageView>(R.id.imageViewLogo)

        // Загрузить анимацию из ресурсов
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)

        // Применить анимацию к ImageView
        logoImageView.startAnimation(scaleAnimation)

        Handler().postDelayed({
            // Запуск AuthenticationActivity после задержки
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            // Закрыть текущую Activity
            finish()
        }, splashTimeOut)
    }
}
