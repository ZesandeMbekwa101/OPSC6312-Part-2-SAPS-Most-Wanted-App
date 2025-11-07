package com.example.sapsmostwantedapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.Report
import com.example.sapsmostwantedapp.data.repository.ReportRepository
import com.example.sapsmostwantedapp.utils.ActivityLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _userReports = MutableStateFlow<List<Report>>(emptyList())
    val userReports: StateFlow<List<Report>> = _userReports.asStateFlow()

    private val _submitResult = MutableLiveData<SubmitResult>()
    val submitResult: LiveData<SubmitResult> = _submitResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUserReports(username: String) {
        viewModelScope.launch {
            reportRepository.getUserReports(username).collect { reports ->
                _userReports.value = reports
            }
        }
    }

    fun submitReport(
        submittedBy: String,
        title: String,
        description: String,
        location: String?,
        reportType: String
    ) {
        if (title.isBlank() || description.isBlank()) {
            _submitResult.value = SubmitResult.Error("Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val report = Report(
                    submittedBy = submittedBy,
                    title = title,
                    description = description,
                    location = location,
                    reportType = reportType
                )
                val result = reportRepository.submitReport(report)
                if (result.isSuccess) {
                    _submitResult.value = SubmitResult.Success("Report submitted successfully!")
                    val reportId = result.getOrNull() ?: 0L
                    ActivityLogger.logReportSubmission(submittedBy, reportId, reportType)
                    loadUserReports(submittedBy)
                } else {
                    _submitResult.value = SubmitResult.Error(
                        result.exceptionOrNull()?.message ?: "Failed to submit report"
                    )
                }
            } catch (e: Exception) {
                _submitResult.value = SubmitResult.Error("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class SubmitResult {
        data class Success(val message: String) : SubmitResult()
        data class Error(val message: String) : SubmitResult()
    }
}





