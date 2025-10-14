package com.example.sapsmostwantedapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.FirebaseDatabase
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sapsmostwantedapp.data.model.User

class RegistrationActivity : ComponentActivity() {
    lateinit var editEmail: EditText
    lateinit var btnRegister: Button
    lateinit var editFirstName: EditText
    lateinit var editLastName: EditText
    lateinit var editPassword: EditText
    lateinit var editUserName: EditText


    private lateinit var navigateToLogLink : TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        LocaleHelper.loadLocale(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        editPassword = findViewById(R.id.passwordText)
        editFirstName = findViewById(R.id.firstNameText)
        editEmail = findViewById(R.id.emailText)
        editLastName = findViewById(R.id.lastNameText)
        editUserName = findViewById(R.id.userNameText)
        btnRegister = findViewById(R.id.registrationBtn)

        navigateToLogLink =  findViewById(R.id.navigateToLoginLk)

        navigateToLogLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            btnRegister.setOnClickListener {
                val firstName = editFirstName.text.toString().trim()
                val lastName = editLastName.text.toString().trim()
                val password = editPassword.text.toString().trim()
                val userName = editUserName.text.toString().trim()
                val email = editEmail.text.toString().trim()

                // Validate that none of the fields are empty
                if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val user = User(firstName, lastName, password, userName, email)

                val db = FirebaseDatabase.getInstance()
                val ref = db.getReference("users")

                ref.child(user.userName).setValue(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
    }








