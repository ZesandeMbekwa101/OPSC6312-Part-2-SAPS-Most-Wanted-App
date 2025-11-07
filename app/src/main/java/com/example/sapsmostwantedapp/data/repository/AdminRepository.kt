package com.example.sapsmostwantedapp.data.repository

import com.example.sapsmostwantedapp.data.dao.ReportDao
import com.example.sapsmostwantedapp.data.dao.UserDao
import com.example.sapsmostwantedapp.data.model.Report
import com.example.sapsmostwantedapp.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val userDao: UserDao,
    private val reportDao: ReportDao
) {
    // User Management
    fun getAllUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(username: String): Result<Unit> {
        return try {
            val user = userDao.getUserByUsername(username)
            if (user != null) {
                userDao.deleteUser(user)
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun makeUserAdmin(username: String): Result<Unit> {
        return try {
            val user = userDao.getUserByUsername(username)
            if (user != null) {
                val updatedUser = user.copy(isAdmin = true)
                userDao.updateUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeAdmin(username: String): Result<Unit> {
        return try {
            val user = userDao.getUserByUsername(username)
            if (user != null) {
                val updatedUser = user.copy(isAdmin = false)
                userDao.updateUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Report Management
    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports()
    }

    fun getReportsByStatus(status: String): Flow<List<Report>> {
        return reportDao.getReportsByStatus(status)
    }

    fun getReportsByUser(username: String): Flow<List<Report>> {
        return reportDao.getReportsByUser(username)
    }

    suspend fun updateReportStatus(
        reportId: Long,
        status: String,
        adminNotes: String?,
        reviewedBy: String
    ): Result<Unit> {
        return try {
            val report = reportDao.getReportById(reportId)
            if (report != null) {
                val updatedReport = report.copy(
                    status = status,
                    adminNotes = adminNotes,
                    reviewedBy = reviewedBy,
                    reviewedAt = System.currentTimeMillis()
                )
                reportDao.updateReport(updatedReport)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: Long): Result<Unit> {
        return try {
            reportDao.deleteReportById(reportId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPendingReportsCount(): Flow<Int> {
        return reportDao.getPendingReportsCount()
    }
}

