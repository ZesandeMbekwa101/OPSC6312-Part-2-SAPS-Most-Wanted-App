package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportSubmissionFragment : Fragment() {

    private lateinit var spinnerReportType: Spinner
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var buttonSubmit: Button
    private val reportViewModel: ReportViewModel by viewModels()
    private var currentUsername: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_submission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUsername = arguments?.getString("username")

        spinnerReportType = view.findViewById(R.id.spinnerReportType)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        buttonSubmit = view.findViewById(R.id.buttonSubmitReport)

        // Setup report type spinner
        val reportTypes = listOf(
            "Crime",
            "Suspicious Activity",
            "Tip",
            "Complaint",
            "Other"
        )
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            reportTypes
        )
        spinnerReportType.adapter = adapter

        buttonSubmit.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val reportType = spinnerReportType.selectedItem.toString()

            if (currentUsername != null) {
                reportViewModel.submitReport(
                    currentUsername!!,
                    title,
                    description,
                    if (location.isNotEmpty()) location else null,
                    reportType
                )
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe submit result
        reportViewModel.submitResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ReportViewModel.SubmitResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    // Clear form
                    editTextTitle.text.clear()
                    editTextDescription.text.clear()
                    editTextLocation.text.clear()
                    spinnerReportType.setSelection(0)
                }
                is ReportViewModel.SubmitResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        // Observe loading state
        reportViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            buttonSubmit.isEnabled = !isLoading
            buttonSubmit.text = if (isLoading) "Submitting..." else "Submit Report"
        })
    }
}


