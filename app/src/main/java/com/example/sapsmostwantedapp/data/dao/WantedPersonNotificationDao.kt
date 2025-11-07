package com.example.sapsmostwantedapp.data.dao

import com.example.sapsmostwantedapp.data.model.WantedPersonNotification
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WantedPersonNotificationDao(private val storageManager: JsonStorageManager) {
    fun getAllNotifications(): Flow<List<WantedPersonNotification>> = flow {
        try {
            val notifications = storageManager.getAllWantedPersonNotifications()
            // Sort: Emergency first, then by date (newest first)
            emit(notifications.sortedWith(compareByDescending<WantedPersonNotification> { it.status == "Emergency" }
                .thenByDescending { it.createdAt }))
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error getting all notifications: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getNotificationsByPersonId(personId: String): Flow<List<WantedPersonNotification>> = flow {
        try {
            val notifications = storageManager.getWantedPersonNotificationsByPersonId(personId)
            emit(notifications.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error getting notifications by person id: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getNotificationsByStatus(status: String): Flow<List<WantedPersonNotification>> = flow {
        try {
            val notifications = storageManager.getWantedPersonNotificationsByStatus(status)
            emit(notifications.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error getting notifications by status: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun getNotificationById(id: Long): WantedPersonNotification? {
        return try {
            storageManager.getWantedPersonNotificationById(id)
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error getting notification by id: ${e.message}", e)
            null
        }
    }

    suspend fun insertNotification(notification: WantedPersonNotification): Long {
        return try {
            storageManager.insertWantedPersonNotification(notification)
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error inserting notification: ${e.message}", e)
            0L
        }
    }

    suspend fun updateNotification(notification: WantedPersonNotification) {
        try {
            storageManager.updateWantedPersonNotification(notification)
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error updating notification: ${e.message}", e)
        }
    }

    suspend fun deleteNotificationById(id: Long) {
        try {
            storageManager.deleteWantedPersonNotificationById(id)
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error deleting notification: ${e.message}", e)
        }
    }

    fun getFoundPersonNotificationsCount(): Flow<Int> = flow {
        try {
            val count = storageManager.getFoundPersonNotificationsCount()
            emit(count)
        } catch (e: Exception) {
            android.util.Log.e("WantedPersonNotificationDao", "Error getting found person notifications count: ${e.message}", e)
            emit(0)
        }
    }
}

