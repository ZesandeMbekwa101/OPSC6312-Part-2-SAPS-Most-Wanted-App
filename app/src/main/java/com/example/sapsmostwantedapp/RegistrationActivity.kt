package com.example.sapsmostwantedapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class RegistrationActivity : ComponentActivity() {

    private lateinit var navigateToLogLink : TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        LocaleHelper.loadLocale(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        navigateToLogLink =  findViewById(R.id.navigateToLoginLk)

        navigateToLogLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}
