package com.example.sapsmostwantedapp.data.dao

import com.example.sapsmostwantedapp.data.model.ActivityLog
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ActivityLogDao(private val storageManager: JsonStorageManager) {
    fun getAllActivityLogs(): Flow<List<ActivityLog>> = flow {
        try {
            val logs = storageManager.getAllActivityLogs()
            emit(logs.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogDao", "Error getting all activity logs: ${e.message}", e)
            emit(emptyList())
        }
        // Flow completes after emitting
    }

    fun getActivityLogsByUser(username: String): Flow<List<ActivityLog>> = flow {
        try {
            val logs = storageManager.getActivityLogsByUser(username)
            emit(logs.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogDao", "Error getting activity logs by user: ${e.message}", e)
            emit(emptyList())
        }
        // Flow completes after emitting
    }

    fun getActivityLogsByAction(action: String): Flow<List<ActivityLog>> = flow {
        try {
            val logs = storageManager.getActivityLogsByAction(action)
            emit(logs.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogDao", "Error getting activity logs by action: ${e.message}", e)
            emit(emptyList())
        }
        // Flow completes after emitting
    }

    suspend fun insertActivityLog(log: ActivityLog): Long {
        return try {
            storageManager.insertActivityLog(log)
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogDao", "Error inserting activity log: ${e.message}", e)
            0L
        }
    }

    suspend fun deleteActivityLogsOlderThan(days: Int) {
        try {
            storageManager.deleteActivityLogsOlderThan(days)
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogDao", "Error deleting old activity logs: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }
}

