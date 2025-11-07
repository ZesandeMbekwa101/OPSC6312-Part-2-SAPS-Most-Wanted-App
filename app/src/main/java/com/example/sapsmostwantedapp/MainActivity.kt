package com.example.sapsmostwantedapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.sapsmostwantedapp.data.dao.ActivityLogDao
import com.example.sapsmostwantedapp.data.repository.AuthRepository
import com.example.sapsmostwantedapp.ui.viewmodel.AuthViewModel
import com.example.sapsmostwantedapp.utils.ActivityLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navigateToRegLink: TextView
    private lateinit var loginButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    
    private val authViewModel: AuthViewModel by viewModels()
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    @Inject
    lateinit var activityLogDao: ActivityLogDao

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.loadLocale(this)

        super.onCreate(savedInstanceState)
        Thread.sleep(5000)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        // Initialize views
        navigateToRegLink = findViewById(R.id.createAccountLink)
        loginButton = findViewById(R.id.loginBtn)
        usernameEditText = findViewById(R.id.userNameText)
        passwordEditText = findViewById(R.id.passwordText)

        // Initialize ActivityLogger (safe initialization)
        try {
            if (::activityLogDao.isInitialized) {
                ActivityLogger.initialize(activityLogDao)
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error initializing ActivityLogger: ${e.message}", e)
        }

        // Ensure admin user exists and has correct credentials
        lifecycleScope.launch {
            try {
                val success = authRepository.ensureAdminUserExists()
                if (success) {
                    android.util.Log.d("MainActivity", "Admin user verified/created successfully")
                } else {
                    android.util.Log.e("MainActivity", "Failed to ensure admin user exists")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error ensuring admin user exists: ${e.message}", e)
            }
        }

        // Navigate to registration
        navigateToRegLink.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Login button click handler
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            
            authViewModel.login(username, password)
        }

        // Observe login result
        authViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.LoginResult.Success -> {
                    val user = result.user
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    
                    // Log successful login (safe call)
                    try {
                        ActivityLogger.logLogin(user.username, true)
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error logging login: ${e.message}", e)
                    }
                    
                    // Check if user is admin
                    if (user.isAdmin) {
                        val intent = Intent(this, AdminDashboardActivity::class.java)
                        intent.putExtra("admin_username", user.username)
                        startActivity(intent)
                        try {
                            ActivityLogger.logNavigation(user.username, "MainActivity", "AdminDashboardActivity")
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error logging navigation: ${e.message}", e)
                        }
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("username", user.username)
                        startActivity(intent)
                        try {
                            ActivityLogger.logNavigation(user.username, "MainActivity", "HomeActivity")
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error logging navigation: ${e.message}", e)
                        }
                    }
                    finish()
                }
                is AuthViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    // Log failed login attempt (safe call)
                    try {
                        val username = usernameEditText.text.toString().trim()
                        if (username.isNotEmpty()) {
                            ActivityLogger.logLogin(username, false)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error logging failed login: ${e.message}", e)
                    }
                }
            }
        })

        // Observe loading state
        authViewModel.isLoading.observe(this, Observer { isLoading ->
            loginButton.isEnabled = !isLoading
            loginButton.text = if (isLoading) "Logging in..." else "Login"
        })
    }
}