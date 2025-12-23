package com.example.dms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dms.models.LoginRequest
import com.example.dms.models.LoginResponse
import com.example.dms.network.RetrofitClient
import com.example.dms.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Если токен есть → на главную
        if (!sessionManager.getToken().isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val email = findViewById<EditText>(R.id.inputEmail)
        val pass = findViewById<EditText>(R.id.inputPassword)
        val btn = findViewById<Button>(R.id.btnLogin)

        btn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = pass.text.toString().trim()

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(emailText, passText)

            RetrofitClient.getInstance().login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val body = response.body()
                        if (response.isSuccessful && body?.token != null && body.user != null) {
                            val token = body.token!!
                            val user = body.user!!

                            Log.d("LoginActivity", "User object from server: $user")
                            Log.d("LoginActivity", "User role from server: '${user.role}'")

                            // --- Сохраняем всё в SharedPreferences ---
                            sessionManager.saveToken(token)
                            sessionManager.saveUserName(user.name)
                            sessionManager.saveUserEmail(user.email)
                            sessionManager.saveUserId(user.id.toString())
                            sessionManager.saveUserRole(user.role ?: "")

                            // --- Логируем сразу после сохранения ---
                            Log.d("LoginActivity", "Saved role: '${sessionManager.getUserRole()}'")

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Log.e("LoginActivity", "Ошибка входа: ${response.code()} / ${response.message()}")
                            Toast.makeText(this@LoginActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginActivity", "Сетевая ошибка: ${t.message}")
                        Toast.makeText(this@LoginActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
