package com.example.opsc6312finalpoe.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    // Replace view binding with explicit view references to avoid generated binding dependency
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnMicrosoft: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // find views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnMicrosoft = findViewById(R.id.btnMicrosoft)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        authRepository = AuthRepository(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google Sign-In - To be implemented", Toast.LENGTH_SHORT).show()
        }

        btnMicrosoft.setOnClickListener {
            testMicrosoftAuth()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        progressBar.visibility = android.view.View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.loginWithEmail(email, password)
            progressBar.visibility = android.view.View.GONE

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to main activity
                val intent = Intent(this@LoginActivity, com.example.opsc6312finalpoe.MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun testMicrosoftAuth() {
        progressBar.visibility = android.view.View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.loginWithMicrosoft()
            progressBar.visibility = android.view.View.GONE

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Microsoft sign-in successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, com.example.opsc6312finalpoe.MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Microsoft sign-in failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}