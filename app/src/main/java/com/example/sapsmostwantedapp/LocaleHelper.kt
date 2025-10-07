package com.example.sapsmostwantedapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

object LocaleHelper {

    fun setLocale(activity: AppCompatActivity, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

        // Save selected language
        val prefs = activity.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        prefs.edit().putString("My_Lang", lang).apply()
    }

    fun loadLocale(activity: AppCompatActivity) {
        val prefs = activity.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        val language = prefs.getString("My_Lang", "en") ?: "en"
        setLocale(activity, language)
    }
}
