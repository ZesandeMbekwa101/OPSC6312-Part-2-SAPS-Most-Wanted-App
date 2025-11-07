package com.example.sapsmostwantedapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sapsmostwantedapp.LocaleHelper
import com.example.sapsmostwantedapp.MainActivity
import com.example.sapsmostwantedapp.R

class SettingsFragment : Fragment() {

    private lateinit var spinnerLanguage: Spinner
    private lateinit var tvLogout: TextView
    private var isFirstSelection = true // 👈 Prevents triggering on initial load

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        spinnerLanguage = view.findViewById(R.id.spinnerLanguage)
        tvLogout = view.findViewById(R.id.tvLogout)

        setupLanguageDropdown()

        tvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupLanguageDropdown() {
        val languages = listOf("English", "isiXhosa")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        spinnerLanguage.adapter = adapter

        // Load saved language preference
        val prefs = requireActivity().getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        val savedLangCode = prefs.getString("My_Lang", "en")

        // Set spinner selection based on saved language
        spinnerLanguage.setSelection(if (savedLangCode == "xh") 1 else 0)

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }

                val selectedLanguage = languages[position]
                val langCode = when (selectedLanguage) {
                    "isiXhosa" -> "xh"
                    else -> "en"
                }

                // ✅ Change and save language
                LocaleHelper.setLocale(requireActivity(), langCode)

                Toast.makeText(requireContext(), "Language set to: $selectedLanguage", Toast.LENGTH_SHORT).show()

                // ✅ Restart MainActivity to apply new language
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
