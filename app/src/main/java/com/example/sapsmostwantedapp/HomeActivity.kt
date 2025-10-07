package com.example.sapsmostwantedapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sapsmostwantedapp.ui.fragments.MostWantedFragment
import com.example.sapsmostwantedapp.ui.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Load default fragment on startup
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_Layout, MostWantedFragment())
            .commit()

        // Handle navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fugitive -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_Layout, MostWantedFragment())
                        .commit()
                    true
                }
                R.id.setting -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_Layout, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadLocale() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val language = prefs.getString("My_Lang", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
