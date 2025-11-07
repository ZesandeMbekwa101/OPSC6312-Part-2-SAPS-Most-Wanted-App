package com.example.sapsmostwantedapp.data.storage

import android.content.Context
import android.util.Log
import com.example.sapsmostwantedapp.data.model.ActivityLog
import com.example.sapsmostwantedapp.data.model.Report
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.data.model.WantedPersonNotification
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JsonStorageManager(private val context: Context) {
    private val gson = Gson()
    private var usersFile: File
    private var reportsFile: File
    private var activityLogsFile: File
    private var wantedPersonNotificationsFile: File
    
    private val usersType = object : TypeToken<List<User>>() {}.type
    private val reportsType = object : TypeToken<List<Report>>() {}.type
    private val activityLogsType = object : TypeToken<List<ActivityLog>>() {}.type
    private val wantedPersonNotificationsType = object : TypeToken<List<WantedPersonNotification>>() {}.type
    
    init {
        try {
            // Ensure files directory exists
            val filesDir = context.filesDir ?: throw IllegalStateException("Files directory is null")
            if (!filesDir.exists()) {
                val created = filesDir.mkdirs()
                if (!created && !filesDir.exists()) {
                    throw IllegalStateException("Failed to create files directory")
                }
            }
            usersFile = File(filesDir, "users.json")
            reportsFile = File(filesDir, "reports.json")
            activityLogsFile = File(filesDir, "activity_logs.json")
            wantedPersonNotificationsFile = File(filesDir, "wanted_person_notifications.json")
            Log.d("JsonStorageManager", "Initialized JSON storage - users: ${usersFile.absolutePath}, reports: ${reportsFile.absolutePath}, logs: ${activityLogsFile.absolutePath}, notifications: ${wantedPersonNotificationsFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error initializing storage: ${e.message}", e)
            // Create fallback files in a safe location
            try {
                val fallbackDir = File(context.cacheDir, "json_storage")
                fallbackDir.mkdirs()
                usersFile = File(fallbackDir, "users.json")
                reportsFile = File(fallbackDir, "reports.json")
                activityLogsFile = File(fallbackDir, "activity_logs.json")
                wantedPersonNotificationsFile = File(fallbackDir, "wanted_person_notifications.json")
                Log.w("JsonStorageManager", "Using fallback storage location: ${fallbackDir.absolutePath}")
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Failed to create fallback storage: ${e2.message}", e2)
                throw e
            }
        }
    }

    // Users operations
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            if (!usersFile.exists() || usersFile.length() == 0L) {
                return@withContext emptyList()
            }
            FileReader(usersFile).use { reader ->
                val jsonString = reader.readText()
                if (jsonString.isBlank()) {
                    return@withContext emptyList()
                }
                val users: List<User>? = gson.fromJson(jsonString, usersType)
                users ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error reading users: ${e.message}", e)
            // If file is corrupted, try to delete and recreate
            try {
                if (usersFile.exists()) {
                    usersFile.delete()
                }
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error deleting corrupted file: ${e2.message}", e2)
            }
            emptyList()
        }
    }

    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        getAllUsers().find { it.username == username }
    }

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        getAllUsers().find { it.email == email }
    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        try {
            val users = getAllUsers().toMutableList()
            val existingIndex = users.indexOfFirst { it.username == user.username }
            if (existingIndex >= 0) {
                users[existingIndex] = user
            } else {
                users.add(user)
            }
            saveUsers(users)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error inserting user: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        try {
            val users = getAllUsers().toMutableList()
            val index = users.indexOfFirst { it.username == user.username }
            if (index >= 0) {
                users[index] = user
                saveUsers(users)
            } else {
                // User not found, nothing to update
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error updating user: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        try {
            val users = getAllUsers().toMutableList()
            users.removeAll { it.username == user.username }
            saveUsers(users)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error deleting user: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    private suspend fun saveUsers(users: List<User>) = withContext(Dispatchers.IO) {
        try {
            // Ensure parent directory exists
            usersFile.parentFile?.mkdirs()
            // Use atomic write: write to temp file first, then rename
            val tempFile = File(usersFile.parent, "${usersFile.name}.tmp")
            FileWriter(tempFile).use { writer ->
                gson.toJson(users, writer)
            }
            // Atomic rename
            if (!tempFile.renameTo(usersFile)) {
                // If rename fails, try direct write
                FileWriter(usersFile).use { writer ->
                    gson.toJson(users, writer)
                }
                tempFile.delete()
            } else {
                // Rename successful, nothing else to do
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error saving users: ${e.message}", e)
            // Try to clean up temp file
            try {
                File(usersFile.parent, "${usersFile.name}.tmp").delete()
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error cleaning up temp file: ${e2.message}", e2)
            }
            // Don't throw - allow app to continue
        }
    }

    // Reports operations
    suspend fun getAllReports(): List<Report> = withContext(Dispatchers.IO) {
        try {
            if (!reportsFile.exists() || reportsFile.length() == 0L) {
                return@withContext emptyList()
            }
            FileReader(reportsFile).use { reader ->
                val jsonString = reader.readText()
                if (jsonString.isBlank()) {
                    return@withContext emptyList()
                }
                val reports: List<Report>? = gson.fromJson(jsonString, reportsType)
                reports ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error reading reports: ${e.message}", e)
            // If file is corrupted, try to delete and recreate
            try {
                if (reportsFile.exists()) {
                    reportsFile.delete()
                }
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error deleting corrupted file: ${e2.message}", e2)
            }
            emptyList()
        }
    }

    suspend fun getReportsByUser(username: String): List<Report> = withContext(Dispatchers.IO) {
        getAllReports().filter { it.submittedBy == username }
    }

    suspend fun getReportsByStatus(status: String): List<Report> = withContext(Dispatchers.IO) {
        getAllReports().filter { it.status == status }
    }

    suspend fun getReportById(reportId: Long): Report? = withContext(Dispatchers.IO) {
        getAllReports().find { it.id == reportId }
    }

    suspend fun insertReport(report: Report): Long = withContext(Dispatchers.IO) {
        try {
            val reports = getAllReports().toMutableList()
            val newId = if (reports.isEmpty()) 1L else (reports.maxOfOrNull { it.id } ?: 0L) + 1L
            val reportWithId = report.copy(id = newId)
            reports.add(reportWithId)
            saveReports(reports)
            newId
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error inserting report: ${e.message}", e)
            // Return a default ID if save fails
            0L
        }
    }

    suspend fun updateReport(report: Report) = withContext(Dispatchers.IO) {
        try {
            val reports = getAllReports().toMutableList()
            val index = reports.indexOfFirst { it.id == report.id }
            if (index >= 0) {
                reports[index] = report
                saveReports(reports)
            } else {
                // Report not found, nothing to update
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error updating report: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    suspend fun deleteReport(report: Report) = withContext(Dispatchers.IO) {
        try {
            val reports = getAllReports().toMutableList()
            reports.removeAll { it.id == report.id }
            saveReports(reports)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error deleting report: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    suspend fun deleteReportById(reportId: Long) = withContext(Dispatchers.IO) {
        try {
            val reports = getAllReports().toMutableList()
            reports.removeAll { it.id == reportId }
            saveReports(reports)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error deleting report: ${e.message}", e)
            // Don't throw, just log - allow app to continue
        }
    }

    suspend fun getPendingReportsCount(): Int = withContext(Dispatchers.IO) {
        getAllReports().count { it.status == "Pending" }
    }

    private suspend fun saveReports(reports: List<Report>) = withContext(Dispatchers.IO) {
        try {
            // Ensure parent directory exists
            reportsFile.parentFile?.mkdirs()
            // Use atomic write: write to temp file first, then rename
            val tempFile = File(reportsFile.parent, "${reportsFile.name}.tmp")
            FileWriter(tempFile).use { writer ->
                gson.toJson(reports, writer)
            }
            // Atomic rename
            if (!tempFile.renameTo(reportsFile)) {
                // If rename fails, try direct write
                FileWriter(reportsFile).use { writer ->
                    gson.toJson(reports, writer)
                }
                tempFile.delete()
            } else {
                // Rename successful, nothing else to do
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error saving reports: ${e.message}", e)
            // Try to clean up temp file
            try {
                File(reportsFile.parent, "${reportsFile.name}.tmp").delete()
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error cleaning up temp file: ${e2.message}", e2)
            }
            // Don't throw - allow app to continue
        }
    }

    // Activity Logs operations
    suspend fun getAllActivityLogs(): List<ActivityLog> = withContext(Dispatchers.IO) {
        try {
            if (!activityLogsFile.exists() || activityLogsFile.length() == 0L) {
                return@withContext emptyList()
            }
            FileReader(activityLogsFile).use { reader ->
                val jsonString = reader.readText()
                if (jsonString.isBlank()) {
                    return@withContext emptyList()
                }
                val logs: List<ActivityLog>? = gson.fromJson(jsonString, activityLogsType)
                logs ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error reading activity logs: ${e.message}", e)
            // If file is corrupted, try to delete and recreate
            try {
                if (activityLogsFile.exists()) {
                    activityLogsFile.delete()
                }
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error deleting corrupted file: ${e2.message}", e2)
            }
            emptyList()
        }
    }

    suspend fun getActivityLogsByUser(username: String): List<ActivityLog> = withContext(Dispatchers.IO) {
        getAllActivityLogs().filter { it.username == username }
    }

    suspend fun getActivityLogsByAction(action: String): List<ActivityLog> = withContext(Dispatchers.IO) {
        getAllActivityLogs().filter { it.action == action }
    }

    suspend fun insertActivityLog(log: ActivityLog): Long = withContext(Dispatchers.IO) {
        try {
            val logs = getAllActivityLogs().toMutableList()
            val newId = if (logs.isEmpty()) 1L else (logs.maxOfOrNull { it.id } ?: 0L) + 1L
            val logWithId = log.copy(id = newId)
            logs.add(logWithId)
            // Keep only last 10000 logs to prevent file from growing too large
            val trimmedLogs = if (logs.size > 10000) {
                logs.sortedByDescending { it.timestamp }.take(10000)
            } else {
                logs
            }
            saveActivityLogs(trimmedLogs)
            newId
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error inserting activity log: ${e.message}", e)
            0L
        }
    }

    suspend fun deleteActivityLogsOlderThan(days: Int) = withContext(Dispatchers.IO) {
        try {
            val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
            val logs = getAllActivityLogs().filter { it.timestamp >= cutoffTime }
            saveActivityLogs(logs)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error deleting old activity logs: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    private suspend fun saveActivityLogs(logs: List<ActivityLog>) = withContext(Dispatchers.IO) {
        try {
            // Ensure parent directory exists
            activityLogsFile.parentFile?.mkdirs()
            // Use atomic write: write to temp file first, then rename
            val tempFile = File(activityLogsFile.parent, "${activityLogsFile.name}.tmp")
            FileWriter(tempFile).use { writer ->
                gson.toJson(logs, writer)
            }
            // Atomic rename
            if (!tempFile.renameTo(activityLogsFile)) {
                // If rename fails, try direct write
                FileWriter(activityLogsFile).use { writer ->
                    gson.toJson(logs, writer)
                }
                tempFile.delete()
            } else {
                // Rename successful, nothing else to do
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error saving activity logs: ${e.message}", e)
            // Try to clean up temp file
            try {
                File(activityLogsFile.parent, "${activityLogsFile.name}.tmp").delete()
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error cleaning up temp file: ${e2.message}", e2)
            }
            // Don't throw - allow app to continue
        }
    }

    // Wanted Person Notifications operations
    suspend fun getAllWantedPersonNotifications(): List<WantedPersonNotification> = withContext(Dispatchers.IO) {
        try {
            if (!wantedPersonNotificationsFile.exists() || wantedPersonNotificationsFile.length() == 0L) {
                return@withContext emptyList()
            }
            FileReader(wantedPersonNotificationsFile).use { reader ->
                val jsonString = reader.readText()
                if (jsonString.isBlank()) {
                    return@withContext emptyList()
                }
                val notifications: List<WantedPersonNotification>? = gson.fromJson(jsonString, wantedPersonNotificationsType)
                notifications ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error reading wanted person notifications: ${e.message}", e)
            try {
                if (wantedPersonNotificationsFile.exists()) {
                    wantedPersonNotificationsFile.delete()
                }
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error deleting corrupted file: ${e2.message}", e2)
            }
            emptyList()
        }
    }

    suspend fun getWantedPersonNotificationsByPersonId(personId: String): List<WantedPersonNotification> = withContext(Dispatchers.IO) {
        getAllWantedPersonNotifications().filter { it.wantedPersonId == personId }
    }

    suspend fun getWantedPersonNotificationsByStatus(status: String): List<WantedPersonNotification> = withContext(Dispatchers.IO) {
        getAllWantedPersonNotifications().filter { it.status == status }
    }

    suspend fun getWantedPersonNotificationById(id: Long): WantedPersonNotification? = withContext(Dispatchers.IO) {
        getAllWantedPersonNotifications().find { it.id == id }
    }

    suspend fun insertWantedPersonNotification(notification: WantedPersonNotification): Long = withContext(Dispatchers.IO) {
        try {
            val notifications = getAllWantedPersonNotifications().toMutableList()
            val newId = if (notifications.isEmpty()) System.currentTimeMillis() else (notifications.maxOfOrNull { it.id } ?: System.currentTimeMillis()) + 1L
            val notificationWithId = notification.copy(id = newId)
            notifications.add(notificationWithId)
            saveWantedPersonNotifications(notifications)
            newId
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error inserting wanted person notification: ${e.message}", e)
            0L
        }
    }

    suspend fun updateWantedPersonNotification(notification: WantedPersonNotification) {
        withContext(Dispatchers.IO) {
            try {
                val notifications = getAllWantedPersonNotifications().toMutableList()
                val index = notifications.indexOfFirst { it.id == notification.id }
                if (index >= 0) {
                    notifications[index] = notification
                    saveWantedPersonNotifications(notifications)
                } else {
                    // Notification not found, nothing to update
                }
            } catch (e: Exception) {
                Log.e("JsonStorageManager", "Error updating wanted person notification: ${e.message}", e)
            }
        }
    }

    suspend fun deleteWantedPersonNotificationById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val notifications = getAllWantedPersonNotifications().toMutableList()
            notifications.removeAll { it.id == id }
            saveWantedPersonNotifications(notifications)
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error deleting wanted person notification: ${e.message}", e)
        }
    }

    suspend fun getFoundPersonNotificationsCount(): Int = withContext(Dispatchers.IO) {
        getAllWantedPersonNotifications().count { it.status == "Found" && !it.isVerified }
    }

    private suspend fun saveWantedPersonNotifications(notifications: List<WantedPersonNotification>) = withContext(Dispatchers.IO) {
        try {
            wantedPersonNotificationsFile.parentFile?.mkdirs()
            val tempFile = File(wantedPersonNotificationsFile.parent, "${wantedPersonNotificationsFile.name}.tmp")
            FileWriter(tempFile).use { writer ->
                gson.toJson(notifications, writer)
            }
            if (!tempFile.renameTo(wantedPersonNotificationsFile)) {
                FileWriter(wantedPersonNotificationsFile).use { writer ->
                    gson.toJson(notifications, writer)
                }
                tempFile.delete()
            } else {
                // Rename successful, nothing else to do
            }
        } catch (e: Exception) {
            Log.e("JsonStorageManager", "Error saving wanted person notifications: ${e.message}", e)
            try {
                File(wantedPersonNotificationsFile.parent, "${wantedPersonNotificationsFile.name}.tmp").delete()
            } catch (e2: Exception) {
                Log.e("JsonStorageManager", "Error cleaning up temp file: ${e2.message}", e2)
            }
        }
    }
}

