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
import com.example.sapsmostwantedapp.ui.adapters.WantedPersonNotificationAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.WantedPersonNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WantedPersonNotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonAll: Button
    private lateinit var buttonEmergency: Button
    private lateinit var buttonFound: Button
    private lateinit var buttonNotFound: Button
    private val notificationViewModel: WantedPersonNotificationViewModel by viewModels()
    private lateinit var notificationAdapter: WantedPersonNotificationAdapter
    private var adminUsername: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wanted_person_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get admin username from parent activity
        adminUsername = (activity as? com.example.sapsmostwantedapp.AdminDashboardActivity)?.let {
            it.intent.getStringExtra("admin_username")
        }

        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        progressBar = view.findViewById(R.id.progressBarNotifications)
        buttonAll = view.findViewById(R.id.buttonAll)
        buttonEmergency = view.findViewById(R.id.buttonEmergency)
        buttonFound = view.findViewById(R.id.buttonFound)
        buttonNotFound = view.findViewById(R.id.buttonNotFound)

        notificationAdapter = WantedPersonNotificationAdapter(
            onVerifyClick = { notificationId ->
                adminUsername?.let {
                    notificationViewModel.verifyNotification(notificationId, it)
                } ?: run {
                    Toast.makeText(requireContext(), "Admin username not found", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { notificationId ->
                notificationViewModel.deleteNotification(notificationId)
            },
            onViewDetailsClick = { notification ->
                showNotificationDetailsDialog(notification)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = notificationAdapter

        // Filter buttons
        buttonAll.setOnClickListener {
            notificationViewModel.loadAllNotifications()
            updateFilterButtons(buttonAll)
        }

        buttonEmergency.setOnClickListener {
            notificationViewModel.loadNotificationsByStatus("Emergency")
            updateFilterButtons(buttonEmergency)
        }

        buttonFound.setOnClickListener {
            notificationViewModel.loadNotificationsByStatus("Found")
            updateFilterButtons(buttonFound)
        }

        buttonNotFound.setOnClickListener {
            notificationViewModel.loadNotificationsByStatus("Not Found")
            updateFilterButtons(buttonNotFound)
        }

        // Observe notifications using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notifications.collect { notifications ->
                    notificationAdapter.submitList(notifications)
                }
            }
        }

        // Observe loading state
        notificationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe operation results
        notificationViewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is WantedPersonNotificationViewModel.SubmitResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is WantedPersonNotificationViewModel.SubmitResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Load all notifications by default
        notificationViewModel.loadAllNotifications()
    }

    private fun updateFilterButtons(selectedButton: Button) {
        val buttons = listOf(buttonAll, buttonEmergency, buttonFound, buttonNotFound)
        buttons.forEach { button ->
            button.isSelected = button == selectedButton
            if (button == selectedButton) {
                button.alpha = 1.0f
            } else {
                button.alpha = 0.6f
            }
        }
    }

    private fun showNotificationDetailsDialog(notification: com.example.sapsmostwantedapp.data.model.WantedPersonNotification) {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        val isEmergency = notification.status == "Emergency"
        val details = buildString {
            if (isEmergency) {
                append("🚨 EMERGENCY ALERT 🚨\n\n")
            }
            append("Person Name: ${notification.wantedPersonName}\n\n")
            append("Person ID: ${notification.wantedPersonId}\n\n")
            append("Status: ${notification.status}\n\n")
            append("Reported by: ${notification.reportedBy}\n\n")
            if (notification.location != null) {
                append("Location: ${notification.location}\n\n")
            }
            if (notification.notes != null) {
                append("Notes: ${notification.notes}\n\n")
            }
            append("Date: ${dateFormat.format(java.util.Date(notification.createdAt))}\n\n")
            if (notification.isVerified) {
                append("Verified: Yes\n")
                if (notification.reviewedBy != null) {
                    append("Reviewed by: ${notification.reviewedBy}\n")
                }
                if (notification.reviewedAt != null) {
                    append("Reviewed at: ${dateFormat.format(java.util.Date(notification.reviewedAt))}")
                }
            } else {
                append("Verified: No")
            }
        }

        val title = if (isEmergency) "🚨 EMERGENCY ALERT 🚨" else "Notification Details"
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show()
    }
}

