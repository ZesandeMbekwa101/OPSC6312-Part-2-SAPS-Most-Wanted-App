package com.example.sapsmostwantedapp.data.model

data class User(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String, // In production, this should be hashed
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

