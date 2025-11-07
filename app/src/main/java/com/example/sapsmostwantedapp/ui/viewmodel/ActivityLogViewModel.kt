package com.example.sapsmostwantedapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.ActivityLog
import com.example.sapsmostwantedapp.data.repository.ActivityLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    private val activityLogRepository: ActivityLogRepository
) : ViewModel() {

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _filteredLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val filteredLogs: StateFlow<List<ActivityLog>> = _filteredLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Don't load in init - let fragments call it explicitly
        // Initialize with empty lists to prevent crashes
        _activityLogs.value = emptyList()
        _filteredLogs.value = emptyList()
    }

    fun loadAllActivityLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val logs = activityLogRepository.getAllActivityLogs()
                _activityLogs.value = logs
                _filteredLogs.value = logs
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogViewModel", "Error loading activity logs: ${e.message}", e)
                _activityLogs.value = emptyList()
                _filteredLogs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByUser(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val logs = activityLogRepository.getActivityLogsByUser(username)
                _filteredLogs.value = logs
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogViewModel", "Error filtering by user: ${e.message}", e)
                _filteredLogs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByAction(action: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val logs = activityLogRepository.getActivityLogsByAction(action)
                _filteredLogs.value = logs
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogViewModel", "Error filtering by action: ${e.message}", e)
                _filteredLogs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilters() {
        _filteredLogs.value = _activityLogs.value
    }

    fun deleteOldLogs(days: Int) {
        viewModelScope.launch {
            try {
                activityLogRepository.deleteActivityLogsOlderThan(days)
                loadAllActivityLogs()
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogViewModel", "Error deleting old logs: ${e.message}", e)
            }
        }
    }
}

