package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sapsmostwantedapp.R

class SettingsFragment : Fragment() {

    private lateinit var spinnerLanguage: Spinner
    private lateinit var tvLogout: TextView
    private var isFirstSelection = true  // 👈 Prevent auto-trigger on load

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

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isFirstSelection) {
                    isFirstSelection = false // 👈 Ignore the first automatic selection
                    return
                }

                val selectedLanguage = languages[position]
                Toast.makeText(requireContext(), "Language set to: $selectedLanguage", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
