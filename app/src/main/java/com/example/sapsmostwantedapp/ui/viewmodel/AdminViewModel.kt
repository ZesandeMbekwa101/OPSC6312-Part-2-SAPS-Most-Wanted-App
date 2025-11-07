package com.example.sapsmostwantedapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.Report
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.data.repository.AdminRepository
import com.example.sapsmostwantedapp.utils.ActivityLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserWithReports(
    val user: User,
    val reports: List<Report>,
    val pendingCount: Int,
    val reviewedCount: Int,
    val resolvedCount: Int,
    val totalCount: Int
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _pendingReportsCount = MutableStateFlow<Int>(0)
    val pendingReportsCount: StateFlow<Int> = _pendingReportsCount.asStateFlow()

    private val _usersWithReports = MutableStateFlow<List<UserWithReports>>(emptyList())
    val usersWithReports: StateFlow<List<UserWithReports>> = _usersWithReports.asStateFlow()

    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentReportsJob: Job? = null
    private var adminUsername: String? = null

    fun setAdminUsername(username: String) {
        adminUsername = username
    }

    init {
        loadUsers()
        loadReports()
        loadPendingReportsCount()
    }

    fun loadUsers() {
        viewModelScope.launch {
            adminRepository.getAllUsersFlow().collect { userList ->
                _users.value = userList
            }
        }
    }

    fun loadReports() {
        currentReportsJob?.cancel()
        currentReportsJob = viewModelScope.launch {
            adminRepository.getAllReports().collect { reportList ->
                _reports.value = reportList
            }
        }
    }

    fun loadReportsByStatus(status: String) {
        currentReportsJob?.cancel()
        currentReportsJob = viewModelScope.launch {
            adminRepository.getReportsByStatus(status).collect { reportList ->
                _reports.value = reportList
            }
        }
    }

    fun loadPendingReportsCount() {
        viewModelScope.launch {
            adminRepository.getPendingReportsCount().collect { count ->
                _pendingReportsCount.value = count
            }
        }
    }

    fun loadUsersWithReports() {
        viewModelScope.launch {
            combine(
                adminRepository.getAllUsersFlow(),
                adminRepository.getAllReports()
            ) { users, reports ->
                users.map { user ->
                    val userReports = reports.filter { it.submittedBy == user.username }
                    val pendingCount = userReports.count { it.status == "Pending" }
                    val reviewedCount = userReports.count { it.status == "Reviewed" }
                    val resolvedCount = userReports.count { it.status == "Resolved" }
                    UserWithReports(
                        user = user,
                        reports = userReports,
                        pendingCount = pendingCount,
                        reviewedCount = reviewedCount,
                        resolvedCount = resolvedCount,
                        totalCount = userReports.size
                    )
                }.sortedByDescending { it.totalCount }
            }.collect { usersWithReports ->
                _usersWithReports.value = usersWithReports
            }
        }
    }

    fun deleteUser(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.deleteUser(username)
            if (result.isSuccess) {
                _operationResult.value = OperationResult.Success("User deleted successfully")
                ActivityLogger.logUserDelete(adminUsername ?: "Admin", username)
                loadUsers()
            } else {
                _operationResult.value = OperationResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to delete user"
                )
            }
            _isLoading.value = false
        }
    }

    fun makeUserAdmin(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.makeUserAdmin(username)
            if (result.isSuccess) {
                _operationResult.value = OperationResult.Success("User granted admin access")
                ActivityLogger.logUserUpdate(adminUsername ?: "Admin", username, "{\"isAdmin\": true}")
                loadUsers()
            } else {
                _operationResult.value = OperationResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update user"
                )
            }
            _isLoading.value = false
        }
    }

    fun removeAdmin(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.removeAdmin(username)
            if (result.isSuccess) {
                _operationResult.value = OperationResult.Success("Admin access removed")
                ActivityLogger.logUserUpdate(adminUsername ?: "Admin", username, "{\"isAdmin\": false}")
                loadUsers()
            } else {
                _operationResult.value = OperationResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update user"
                )
            }
            _isLoading.value = false
        }
    }

    fun updateReportStatus(
        reportId: Long,
        status: String,
        adminNotes: String?,
        reviewedBy: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.updateReportStatus(reportId, status, adminNotes, reviewedBy)
            if (result.isSuccess) {
                _operationResult.value = OperationResult.Success("Report status updated")
                ActivityLogger.logReportUpdate(reviewedBy, reportId, status)
                loadReports()
                loadPendingReportsCount()
            } else {
                _operationResult.value = OperationResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update report"
                )
            }
            _isLoading.value = false
        }
    }

    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.deleteReport(reportId)
            if (result.isSuccess) {
                _operationResult.value = OperationResult.Success("Report deleted successfully")
                ActivityLogger.logReportDelete(adminUsername ?: "Admin", reportId)
                loadReports()
                loadPendingReportsCount()
            } else {
                _operationResult.value = OperationResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to delete report"
                )
            }
            _isLoading.value = false
        }
    }

    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}




