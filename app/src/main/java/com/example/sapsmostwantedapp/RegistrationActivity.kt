package com.example.sapsmostwantedapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.sapsmostwantedapp.ui.viewmodel.AuthViewModel
import com.example.sapsmostwantedapp.utils.ActivityLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : ComponentActivity() {

    private lateinit var navigateToLogLink: TextView
    private lateinit var registrationButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.loadLocale(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize views
        navigateToLogLink = findViewById(R.id.navigateToLoginLk)
        registrationButton = findViewById(R.id.registrationBtn)
        firstNameEditText = findViewById(R.id.firstNameText)
        lastNameEditText = findViewById(R.id.lastNameText)
        usernameEditText = findViewById(R.id.userNameText)
        passwordEditText = findViewById(R.id.passwordText)
        emailEditText = findViewById(R.id.emailText)

        // Navigate to login
        navigateToLogLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Registration button click handler
        registrationButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            
            authViewModel.register(firstName, lastName, username, password, email)
        }

        // Observe registration result
        authViewModel.registrationResult.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.RegistrationResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    // Log successful registration
                    val username = usernameEditText.text.toString().trim()
                    val email = emailEditText.text.toString().trim()
                    ActivityLogger.logRegister(username, email)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthViewModel.RegistrationResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        // Observe loading state
        authViewModel.isLoading.observe(this, Observer { isLoading ->
            registrationButton.isEnabled = !isLoading
            registrationButton.text = if (isLoading) "Registering..." else "Sign Up"
        })
    }
}
