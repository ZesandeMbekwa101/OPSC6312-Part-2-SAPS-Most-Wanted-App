package com.example.sapsmostwantedapp.data.repository

import com.example.sapsmostwantedapp.data.dao.ReportDao
import com.example.sapsmostwantedapp.data.model.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    suspend fun submitReport(report: Report): Result<Long> {
        return try {
            val reportId = reportDao.insertReport(report)
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserReports(username: String): Flow<List<Report>> {
        return reportDao.getReportsByUser(username)
    }

    suspend fun getReportById(reportId: Long): Report? {
        return reportDao.getReportById(reportId)
    }
}






