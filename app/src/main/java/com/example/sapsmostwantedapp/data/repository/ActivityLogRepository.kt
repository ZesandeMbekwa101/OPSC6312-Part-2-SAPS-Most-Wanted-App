package com.example.sapsmostwantedapp.data.repository

import com.example.sapsmostwantedapp.data.dao.ActivityLogDao
import com.example.sapsmostwantedapp.data.model.ActivityLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepository @Inject constructor(
    private val activityLogDao: ActivityLogDao
) {
    suspend fun getAllActivityLogs(): List<ActivityLog> {
        return try {
            activityLogDao.getAllActivityLogs().first()
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogRepository", "Error getting all activity logs: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getActivityLogsByUser(username: String): List<ActivityLog> {
        return try {
            activityLogDao.getActivityLogsByUser(username).first()
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogRepository", "Error getting activity logs by user: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getActivityLogsByAction(action: String): List<ActivityLog> {
        return try {
            activityLogDao.getActivityLogsByAction(action).first()
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogRepository", "Error getting activity logs by action: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun deleteActivityLogsOlderThan(days: Int) {
        activityLogDao.deleteActivityLogsOlderThan(days)
    }
}

