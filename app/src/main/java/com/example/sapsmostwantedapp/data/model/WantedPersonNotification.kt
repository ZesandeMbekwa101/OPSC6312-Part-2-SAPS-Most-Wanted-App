package com.example.sapsmostwantedapp.data.model

import java.io.Serializable

data class WantedPersonNotification(
    val id: Long = System.currentTimeMillis(),
    val wantedPersonId: String, // ID of the wanted person
    val wantedPersonName: String, // Name of the wanted person
    val reportedBy: String, // Username of the user who reported
    val status: String, // "Found" or "Not Found"
    val location: String? = null, // Optional location where person was seen
    val notes: String? = null, // Optional additional notes
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedBy: String? = null, // Admin who reviewed the notification
    val reviewedAt: Long? = null,
    val isVerified: Boolean = false // Whether admin verified the notification
) : Serializable

