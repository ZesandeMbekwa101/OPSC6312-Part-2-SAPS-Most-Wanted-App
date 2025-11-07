package com.example.sapsmostwantedapp.data.dao

import com.example.sapsmostwantedapp.data.model.Report
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReportDao(private val storageManager: JsonStorageManager) {
    fun getAllReports(): Flow<List<Report>> = flow {
        try {
            val reports = storageManager.getAllReports()
            emit(reports.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error getting all reports: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getReportsByUser(username: String): Flow<List<Report>> = flow {
        try {
            val reports = storageManager.getReportsByUser(username)
            emit(reports.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error getting reports by user: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getReportsByStatus(status: String): Flow<List<Report>> = flow {
        try {
            val reports = storageManager.getReportsByStatus(status)
            emit(reports.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error getting reports by status: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun getReportById(reportId: Long): Report? {
        return try {
            storageManager.getReportById(reportId)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error getting report by id: ${e.message}", e)
            null
        }
    }

    suspend fun insertReport(report: Report): Long {
        return try {
            storageManager.insertReport(report)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error inserting report: ${e.message}", e)
            0L
        }
    }

    suspend fun updateReport(report: Report) {
        try {
            storageManager.updateReport(report)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error updating report: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    suspend fun deleteReport(report: Report) {
        try {
            storageManager.deleteReport(report)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error deleting report: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    suspend fun deleteReportById(reportId: Long) {
        try {
            storageManager.deleteReportById(reportId)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error deleting report by id: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    fun getPendingReportsCount(): Flow<Int> = flow {
        try {
            val count = storageManager.getPendingReportsCount()
            emit(count)
        } catch (e: Exception) {
            android.util.Log.e("ReportDao", "Error getting pending reports count: ${e.message}", e)
            emit(0)
        }
    }
}





