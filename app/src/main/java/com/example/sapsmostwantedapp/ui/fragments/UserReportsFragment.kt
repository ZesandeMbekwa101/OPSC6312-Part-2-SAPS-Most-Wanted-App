package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.sapsmostwantedapp.ui.adapters.UserReportsAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.AdminViewModel
import com.example.sapsmostwantedapp.ui.viewmodel.WantedPersonNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserReportsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adminViewModel: AdminViewModel by viewModels()
    private val notificationViewModel: WantedPersonNotificationViewModel by viewModels()
    private lateinit var userReportsAdapter: UserReportsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewUserReports)
        progressBar = view.findViewById(R.id.progressBarUserReports)

        userReportsAdapter = UserReportsAdapter(
            onViewReportsClick = { username ->
                // Navigate to reports filtered by this user
                showUserReportsDialog(username)
            },
            onViewUserClick = { user ->
                // Show user details
                showUserDetailsDialog(user)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userReportsAdapter

        // Observe users with reports using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.usersWithReports.collect { usersWithReports ->
                    userReportsAdapter.submitList(usersWithReports)
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

        // Load users with reports
        adminViewModel.loadUsersWithReports()
        
        // Load notifications to show emergency alerts in user details
        notificationViewModel.loadAllNotifications()
    }

    private fun showUserReportsDialog(username: String) {
        val userWithReports = adminViewModel.usersWithReports.value.find { it.user.username == username }
        if (userWithReports != null && userWithReports.reports.isNotEmpty()) {
            val reportsText = userWithReports.reports.joinToString("\n\n") { report ->
                "Title: ${report.title}\n" +
                "Type: ${report.reportType}\n" +
                "Status: ${report.status}\n" +
                "Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(report.createdAt))}"
            }
            
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reports by ${userWithReports.user.username}")
                .setMessage(reportsText)
                .setPositiveButton("OK", null)
                .show()
        } else {
            Toast.makeText(requireContext(), "No reports found for this user", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUserDetailsDialog(user: com.example.sapsmostwantedapp.data.model.User) {
        val userWithReports = adminViewModel.usersWithReports.value.find { it.user.username == user.username }
        
        // Get emergency notifications for this user
        val emergencyNotifications = notificationViewModel.notifications.value
            .filter { it.reportedBy == user.username && it.status == "Emergency" }
        
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        
        val userInfo = buildString {
            append("Username: ${user.username}\n\n")
            append("Name: ${user.firstName} ${user.lastName}\n\n")
            append("Email: ${user.email}\n\n")
            append("Admin: ${if (user.isAdmin) "Yes" else "No"}\n\n")
            if (userWithReports != null) {
                append("Total Reports: ${userWithReports.totalCount}\n")
                append("Pending: ${userWithReports.pendingCount}\n")
                append("Reviewed: ${userWithReports.reviewedCount}\n")
                append("Resolved: ${userWithReports.resolvedCount}\n\n")
            }
            
            // Show emergency notifications
            if (emergencyNotifications.isNotEmpty()) {
                append("🚨 EMERGENCY NOTIFICATIONS (${emergencyNotifications.size}):\n\n")
                emergencyNotifications.forEachIndexed { index, notification ->
                    append("Emergency ${index + 1}:\n")
                    append("Person: ${notification.wantedPersonName}\n")
                    if (notification.location != null) {
                        append("Location: ${notification.location}\n")
                    }
                    if (notification.notes != null) {
                        append("Notes: ${notification.notes}\n")
                    }
                    append("Date: ${dateFormat.format(java.util.Date(notification.createdAt))}\n")
                    append("Verified: ${if (notification.isVerified) "Yes" else "No"}\n")
                    if (index < emergencyNotifications.size - 1) {
                        append("\n")
                    }
                }
            } else {
                append("Emergency Notifications: 0")
            }
        }
        
        val title = if (emergencyNotifications.isNotEmpty()) {
            "🚨 User Information - ${emergencyNotifications.size} Emergency Alert(s) 🚨"
        } else {
            "User Information"
        }
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(userInfo)
            .setPositiveButton("OK", null)
            .show()
    }
}

