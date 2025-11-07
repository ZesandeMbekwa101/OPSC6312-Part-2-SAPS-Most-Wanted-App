package com.example.sapsmostwantedapp.data.repository

import com.example.sapsmostwantedapp.data.dao.WantedPersonNotificationDao
import com.example.sapsmostwantedapp.data.model.WantedPersonNotification
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WantedPersonNotificationRepository @Inject constructor(
    private val notificationDao: WantedPersonNotificationDao
) {
    suspend fun submitNotification(notification: WantedPersonNotification): Result<Long> {
        return try {
            val notificationId = notificationDao.insertNotification(notification)
            Result.success(notificationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllNotifications(): Flow<List<WantedPersonNotification>> {
        return notificationDao.getAllNotifications()
    }

    fun getNotificationsByPersonId(personId: String): Flow<List<WantedPersonNotification>> {
        return notificationDao.getNotificationsByPersonId(personId)
    }

    fun getNotificationsByStatus(status: String): Flow<List<WantedPersonNotification>> {
        return notificationDao.getNotificationsByStatus(status)
    }

    suspend fun getNotificationById(id: Long): WantedPersonNotification? {
        return notificationDao.getNotificationById(id)
    }

    suspend fun updateNotification(notification: WantedPersonNotification): Result<Unit> {
        return try {
            notificationDao.updateNotification(notification)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(id: Long): Result<Unit> {
        return try {
            notificationDao.deleteNotificationById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFoundPersonNotificationsCount(): Flow<Int> {
        return notificationDao.getFoundPersonNotificationsCount()
    }
}

