package com.example.sapsmostwantedapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.WantedPersonNotification
import com.example.sapsmostwantedapp.data.repository.WantedPersonNotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WantedPersonNotificationViewModel @Inject constructor(
    private val notificationRepository: WantedPersonNotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<WantedPersonNotification>>(emptyList())
    val notifications: StateFlow<List<WantedPersonNotification>> = _notifications.asStateFlow()

    private val _foundNotificationsCount = MutableStateFlow<Int>(0)
    val foundNotificationsCount: StateFlow<Int> = _foundNotificationsCount.asStateFlow()

    private val _submitResult = MutableLiveData<SubmitResult>()
    val submitResult: LiveData<SubmitResult> = _submitResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadAllNotifications()
        loadFoundNotificationsCount()
    }

    fun loadAllNotifications() {
        viewModelScope.launch {
            notificationRepository.getAllNotifications().collect { notifications ->
                _notifications.value = notifications
            }
        }
    }

    fun loadNotificationsByStatus(status: String) {
        viewModelScope.launch {
            notificationRepository.getNotificationsByStatus(status).collect { notifications ->
                _notifications.value = notifications
            }
        }
    }

    fun loadFoundNotificationsCount() {
        viewModelScope.launch {
            notificationRepository.getFoundPersonNotificationsCount().collect { count ->
                _foundNotificationsCount.value = count
            }
        }
    }

    fun submitNotification(
        wantedPersonId: String,
        wantedPersonName: String,
        reportedBy: String,
        status: String,
        location: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val notification = WantedPersonNotification(
                    wantedPersonId = wantedPersonId,
                    wantedPersonName = wantedPersonName,
                    reportedBy = reportedBy,
                    status = status,
                    location = location,
                    notes = notes
                )
                val result = notificationRepository.submitNotification(notification)
                if (result.isSuccess) {
                    _submitResult.value = SubmitResult.Success("Notification submitted successfully!")
                    loadAllNotifications()
                    loadFoundNotificationsCount()
                } else {
                    _submitResult.value = SubmitResult.Error(
                        result.exceptionOrNull()?.message ?: "Failed to submit notification"
                    )
                }
            } catch (e: Exception) {
                _submitResult.value = SubmitResult.Error("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyNotification(notificationId: Long, reviewedBy: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val notification = notificationRepository.getNotificationById(notificationId)
                if (notification != null) {
                    val updatedNotification = notification.copy(
                        isVerified = true,
                        reviewedBy = reviewedBy,
                        reviewedAt = System.currentTimeMillis()
                    )
                    val result = notificationRepository.updateNotification(updatedNotification)
                    if (result.isSuccess) {
                        _submitResult.value = SubmitResult.Success("Notification verified successfully!")
                        loadAllNotifications()
                        loadFoundNotificationsCount()
                    } else {
                        _submitResult.value = SubmitResult.Error("Failed to verify notification")
                    }
                } else {
                    _submitResult.value = SubmitResult.Error("Notification not found")
                }
            } catch (e: Exception) {
                _submitResult.value = SubmitResult.Error("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = notificationRepository.deleteNotification(notificationId)
                if (result.isSuccess) {
                    _submitResult.value = SubmitResult.Success("Notification deleted successfully!")
                    loadAllNotifications()
                    loadFoundNotificationsCount()
                } else {
                    _submitResult.value = SubmitResult.Error("Failed to delete notification")
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

