package com.example.sapsmostwantedapp.data.model

data class Report(
    val id: Long = 0,
    val submittedBy: String, // username of the user who submitted
    val title: String,
    val description: String,
    val location: String? = null,
    val reportType: String, // e.g., "Crime", "Suspicious Activity", "Tip", etc.
    val status: String = "Pending", // "Pending", "Reviewed", "Resolved"
    val adminNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewedBy: String? = null // admin username who reviewed
)





