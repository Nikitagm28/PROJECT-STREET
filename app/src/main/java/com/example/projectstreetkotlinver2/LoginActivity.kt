package com.example.projectstreetkotlinver2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectstreetkotlinver2.BasicActivity
import com.example.projectstreetkotlinver2.R
import com.example.projectstreetkotlinver2.RegisterActivity
import com.example.projectstreetkotlinver2.network.RetrofitClient
import com.example.projectstreetkotlinver2.network.UserLoginRequest
import com.example.projectstreetkotlinver2.network.UserLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                performLogin(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(username: String, password: String) {
        val apiService = RetrofitClient.apiService
        val loginRequest = UserLoginRequest(username, password)

        apiService.getToken(loginRequest).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        saveCredentials(username, loginResponse.access)
                        Log.d("LoginActivity", "Saved username: $username, token: ${loginResponse.access}")
                        navigateToMainScreen()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveCredentials(username: String, accessToken: String) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("USERNAME", username)
            putString("ACCESS_TOKEN", accessToken)
            apply()
        }
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, BasicActivity::class.java) // Замените на ваше основное Activity
        startActivity(intent)
        finish()
    }
}
