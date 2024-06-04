package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.projectstreetkotlinver2.ui.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val logoImageView = findViewById<ImageView>(R.id.imageViewLogo)


        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)


        logoImageView.startAnimation(scaleAnimation)

        Handler().postDelayed({

            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }, splashTimeOut)
    }
}
