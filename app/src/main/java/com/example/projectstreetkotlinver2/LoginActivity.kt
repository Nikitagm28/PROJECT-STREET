package com.example.projectstreetkotlinver2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val enterButton = findViewById<Button>(R.id.login_button)

        enterButton.setOnClickListener {
            val brandsIntent = Intent(this, BasicActivity::class.java)
            startActivity(brandsIntent)
        }
    }
}