package com.example.sapsmostwantedapp.data.model

data class ActivityLog(
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val username: String,
    val action: String, // e.g., "LOGIN", "LOGOUT", "REGISTER", "SUBMIT_REPORT", "UPDATE_REPORT", "DELETE_USER", etc.
    val description: String,
    val details: String? = null, // Additional details in JSON format
    val ipAddress: String? = null,
    val userAgent: String? = null
)




