package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.ui.adapters.ReportAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonAll: Button
    private lateinit var buttonPending: Button
    private lateinit var buttonReviewed: Button
    private lateinit var buttonResolved: Button
    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var reportAdapter: ReportAdapter
    private var adminUsername: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reports_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get admin username from parent activity
        adminUsername = (activity as? com.example.sapsmostwantedapp.AdminDashboardActivity)?.let {
            it.intent.getStringExtra("admin_username")
        }

        recyclerView = view.findViewById(R.id.recyclerViewReports)
        progressBar = view.findViewById(R.id.progressBarReports)
        buttonAll = view.findViewById(R.id.buttonAll)
        buttonPending = view.findViewById(R.id.buttonPending)
        buttonReviewed = view.findViewById(R.id.buttonReviewed)
        buttonResolved = view.findViewById(R.id.buttonResolved)

        reportAdapter = ReportAdapter(
            onDeleteClick = { reportId ->
                adminViewModel.deleteReport(reportId)
            },
            onViewDetailsClick = { report ->
                // Show report details dialog with update option
                showReportDetailsDialog(report)
            },
            onUpdateStatusClick = { reportId, status, adminNotes, reviewedBy ->
                adminViewModel.updateReportStatus(reportId, status, adminNotes, reviewedBy ?: adminUsername ?: "Admin")
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = reportAdapter

        // Filter buttons
        buttonAll.setOnClickListener {
            adminViewModel.loadReports()
            updateFilterButtons(buttonAll)
        }

        buttonPending.setOnClickListener {
            adminViewModel.loadReportsByStatus("Pending")
            updateFilterButtons(buttonPending)
        }

        buttonReviewed.setOnClickListener {
            adminViewModel.loadReportsByStatus("Reviewed")
            updateFilterButtons(buttonReviewed)
        }

        buttonResolved.setOnClickListener {
            adminViewModel.loadReportsByStatus("Resolved")
            updateFilterButtons(buttonResolved)
        }

        // Observe reports using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.reports.collect { reports ->
                    reportAdapter.submitList(reports)
                }
            }
        }

        // Observe loading state
        adminViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe operation results
        adminViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AdminViewModel.OperationResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is AdminViewModel.OperationResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Load all reports by default
        adminViewModel.loadReports()
    }

    private fun updateFilterButtons(selectedButton: Button) {
        val buttons = listOf(buttonAll, buttonPending, buttonReviewed, buttonResolved)
        buttons.forEach { button ->
            button.isSelected = button == selectedButton
        }
    }

    private fun showReportDetailsDialog(report: com.example.sapsmostwantedapp.data.model.Report) {
        val view = layoutInflater.inflate(R.layout.dialog_report_update, null)
        val spinnerStatus = view.findViewById<android.widget.Spinner>(R.id.spinnerStatus)
        val editTextNotes = view.findViewById<android.widget.EditText>(R.id.editTextAdminNotes)
        
        // Setup status spinner
        val statuses = listOf("Pending", "Reviewed", "Resolved")
        val statusAdapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            statuses
        )
        spinnerStatus.adapter = statusAdapter
        
        // Set current status
        val currentStatusIndex = statuses.indexOf(report.status)
        if (currentStatusIndex >= 0) {
            spinnerStatus.setSelection(currentStatusIndex)
        }
        
        // Set existing notes if any
        if (report.adminNotes != null) {
            editTextNotes.setText(report.adminNotes)
        }
        
        // Create and show dialog
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(report.title)
            .setView(view)
            .setPositiveButton("Update") { _, _ ->
                val newStatus = spinnerStatus.selectedItem.toString()
                val notes = editTextNotes.text.toString().trim()
                adminViewModel.updateReportStatus(
                    report.id,
                    newStatus,
                    if (notes.isNotEmpty()) notes else null,
                    adminUsername ?: "Admin"
                )
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("View Details") { _, _ ->
                // Show read-only details
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle(report.title)
                    .setMessage(
                        "Type: ${report.reportType}\n\n" +
                        "Description: ${report.description}\n\n" +
                        "Location: ${report.location ?: "Not specified"}\n\n" +
                        "Submitted by: ${report.submittedBy}\n\n" +
                        "Status: ${report.status}\n\n" +
                        "Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(report.createdAt))}\n\n" +
                        if (report.adminNotes != null) "Admin Notes: ${report.adminNotes}\n" else ""
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
            .show()
    }
}

