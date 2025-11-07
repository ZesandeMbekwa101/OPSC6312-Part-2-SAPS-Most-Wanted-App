package com.example.sapsmostwantedapp.utils

import android.content.Context
import android.util.Log
import com.example.sapsmostwantedapp.data.dao.ActivityLogDao
import com.example.sapsmostwantedapp.data.model.ActivityLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ActivityLogger {
    private var activityLogDao: ActivityLogDao? = null
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(dao: ActivityLogDao) {
        try {
            activityLogDao = dao
            isInitialized = true
            Log.d("ActivityLogger", "ActivityLogger initialized successfully")
        } catch (e: Exception) {
            Log.e("ActivityLogger", "Error initializing ActivityLogger: ${e.message}", e)
            isInitialized = false
        }
    }

    fun log(
        username: String,
        action: String,
        description: String,
        details: String? = null
    ) {
        try {
            if (!isInitialized || activityLogDao == null) {
                Log.w("ActivityLogger", "ActivityLogger not initialized. Skipping log: $action")
                return
            }

            scope.launch {
                try {
                    val log = ActivityLog(
                        username = username,
                        action = action,
                        description = description,
                        details = details,
                        timestamp = System.currentTimeMillis()
                    )
                    activityLogDao?.insertActivityLog(log)
                } catch (e: Exception) {
                    Log.e("ActivityLogger", "Error logging activity: ${e.message}", e)
                    // Don't rethrow - just log the error
                }
            }
        } catch (e: Exception) {
            Log.e("ActivityLogger", "Error in log function: ${e.message}", e)
            // Don't rethrow - prevent crashes
        }
    }

    // Convenience methods for common actions
    fun logLogin(username: String, success: Boolean) {
        log(
            username = username,
            action = "LOGIN",
            description = if (success) "User logged in successfully" else "Login attempt failed",
            details = "{\"success\": $success}"
        )
    }

    fun logLogout(username: String) {
        log(
            username = username,
            action = "LOGOUT",
            description = "User logged out"
        )
    }

    fun logRegister(username: String, email: String) {
        log(
            username = username,
            action = "REGISTER",
            description = "New user registered",
            details = "{\"email\": \"$email\"}"
        )
    }

    fun logReportSubmission(username: String, reportId: Long, reportType: String) {
        log(
            username = username,
            action = "SUBMIT_REPORT",
            description = "User submitted a report",
            details = "{\"reportId\": $reportId, \"reportType\": \"$reportType\"}"
        )
    }

    fun logReportUpdate(adminUsername: String, reportId: Long, status: String) {
        log(
            username = adminUsername,
            action = "UPDATE_REPORT",
            description = "Admin updated report status",
            details = "{\"reportId\": $reportId, \"status\": \"$status\"}"
        )
    }

    fun logReportDelete(adminUsername: String, reportId: Long) {
        log(
            username = adminUsername,
            action = "DELETE_REPORT",
            description = "Admin deleted a report",
            details = "{\"reportId\": $reportId}"
        )
    }

    fun logUserDelete(adminUsername: String, deletedUsername: String) {
        log(
            username = adminUsername,
            action = "DELETE_USER",
            description = "Admin deleted a user",
            details = "{\"deletedUsername\": \"$deletedUsername\"}"
        )
    }

    fun logUserUpdate(adminUsername: String, updatedUsername: String, changes: String) {
        log(
            username = adminUsername,
            action = "UPDATE_USER",
            description = "Admin updated user information",
            details = "{\"updatedUsername\": \"$updatedUsername\", \"changes\": $changes}"
        )
    }

    fun logAdminAction(adminUsername: String, action: String, description: String, details: String? = null) {
        log(
            username = adminUsername,
            action = action,
            description = description,
            details = details
        )
    }

    fun logNavigation(username: String, fromScreen: String, toScreen: String) {
        log(
            username = username,
            action = "NAVIGATION",
            description = "User navigated between screens",
            details = "{\"from\": \"$fromScreen\", \"to\": \"$toScreen\"}"
        )
    }
}

