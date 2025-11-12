package com.example.opsc6312finalpoe.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc6312finalpoe.databinding.ActivityLoginBinding
import com.example.opsc6312finalpoe.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google Sign-In - To be implemented", Toast.LENGTH_SHORT).show()
        }

        binding.btnMicrosoft.setOnClickListener {
            testMicrosoftAuth()
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.loginWithEmail(email, password)
            binding.progressBar.visibility = android.view.View.GONE

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to main activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun testMicrosoftAuth() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.loginWithMicrosoft()
            binding.progressBar.visibility = android.view.View.GONE

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Microsoft sign-in successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Microsoft sign-in failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}