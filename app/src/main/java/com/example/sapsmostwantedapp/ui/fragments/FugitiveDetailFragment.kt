package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.WantedPerson
import com.example.sapsmostwantedapp.ui.viewmodel.WantedPersonNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FugitiveDetailFragment : Fragment() {

    private val notificationViewModel: WantedPersonNotificationViewModel by viewModels()
    private var currentUsername: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fugitive_detail, container, false)

        val person = arguments?.getSerializable("person") as? WantedPerson ?: return view
        currentUsername = arguments?.getString("username")

        val imageView = view.findViewById<ImageView>(R.id.ivFugitive)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvCrime = view.findViewById<TextView>(R.id.tvCrime)
        val tvCaseNumber = view.findViewById<TextView>(R.id.tvCaseNumber)
        val tvGender = view.findViewById<TextView>(R.id.tvGender)
        val tvHairColor = view.findViewById<TextView>(R.id.tvHairColor)
        val tvStation = view.findViewById<TextView>(R.id.tvStation)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val buttonFound = view.findViewById<Button>(R.id.buttonFound)
        val buttonNotFound = view.findViewById<Button>(R.id.buttonNotFound)
        val buttonEmergency = view.findViewById<Button>(R.id.buttonEmergency)

        // Set data dynamically from model
        val personName = person.properties?.name?.firstOrNull() ?: person.caption ?: "Unknown"
        val personId = person.id ?: "Unknown"
        
        tvName.text = personName
        tvCrime.text = "Crime: ${person.properties?.topics?.joinToString() ?: "Unknown"}"
        tvCaseNumber.text = "Notes: ${person.properties?.notes?.joinToString() ?: "None"}"
        tvGender.text = "Gender: ${person.properties?.gender?.firstOrNull() ?: "Unknown"}"
        tvHairColor.text = "Hair Colour: ${person.properties?.hairColor ?: "Unknown"}"
        tvStation.text = "Country: ${person.properties?.country?.firstOrNull() ?: "Unknown"}"
        tvEmail.text = "Source: ${person.properties?.sourceUrl?.firstOrNull() ?: "N/A"}"
        tvPhone.text = "ID: $personId"

        // Optional image loading if available
        try {
            Glide.with(this)
                .load("https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png")
                .placeholder(R.drawable.ic_person)
                .into(imageView)
        } catch (e: Exception) {
            // If Glide fails, just use placeholder
            imageView.setImageResource(R.drawable.ic_person)
        }

        // Button click handlers - with null safety
        buttonFound?.setOnClickListener {
            showNotificationDialog(personId, personName, "Found")
        }

        buttonNotFound?.setOnClickListener {
            showNotificationDialog(personId, personName, "Not Found")
        }

        buttonEmergency?.setOnClickListener {
            showEmergencyDialog(personId, personName)
        }

        // Observe submit result
        notificationViewModel.submitResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is WantedPersonNotificationViewModel.SubmitResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is WantedPersonNotificationViewModel.SubmitResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        return view
    }

    private fun showNotificationDialog(personId: String, personName: String, status: String) {
        if (currentUsername == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = layoutInflater.inflate(R.layout.dialog_wanted_person_notification, null)
        val editTextLocation = view.findViewById<EditText>(R.id.editTextLocation)
        val editTextNotes = view.findViewById<EditText>(R.id.editTextNotes)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Report Person $status")
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                val location = editTextLocation.text.toString().trim()
                val notes = editTextNotes.text.toString().trim()
                
                notificationViewModel.submitNotification(
                    wantedPersonId = personId,
                    wantedPersonName = personName,
                    reportedBy = currentUsername!!,
                    status = status,
                    location = if (location.isNotEmpty()) location else null,
                    notes = if (notes.isNotEmpty()) notes else null
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEmergencyDialog(personId: String, personName: String) {
        if (currentUsername == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = layoutInflater.inflate(R.layout.dialog_wanted_person_notification, null)
        val editTextLocation = view.findViewById<EditText>(R.id.editTextLocation)
        val editTextNotes = view.findViewById<EditText>(R.id.editTextNotes)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🚨 EMERGENCY ALERT 🚨")
            .setMessage("This is an EMERGENCY alert. Please provide location and details immediately!")
            .setView(view)
            .setPositiveButton("SEND EMERGENCY") { _, _ ->
                val location = editTextLocation.text.toString().trim()
                val notes = editTextNotes.text.toString().trim()
                
                if (location.isEmpty()) {
                    Toast.makeText(requireContext(), "Please provide location for emergency alert!", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                
                notificationViewModel.submitNotification(
                    wantedPersonId = personId,
                    wantedPersonName = personName,
                    reportedBy = currentUsername!!,
                    status = "Emergency",
                    location = location,
                    notes = if (notes.isNotEmpty()) notes else "EMERGENCY ALERT - Immediate attention required!"
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        fun newInstance(person: WantedPerson, username: String? = null): FugitiveDetailFragment {
            val fragment = FugitiveDetailFragment()
            val args = Bundle()
            args.putSerializable("person", person)
            username?.let { args.putString("username", it) }
            fragment.arguments = args
            return fragment
        }
    }
}
