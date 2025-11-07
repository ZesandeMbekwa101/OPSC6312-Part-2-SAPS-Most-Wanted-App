package com.example.sapsmostwantedapp.data.dao

import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserDao(private val storageManager: JsonStorageManager) {
    suspend fun getUserByUsername(username: String): User? {
        return try {
            storageManager.getUserByUsername(username)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error getting user by username: ${e.message}", e)
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            storageManager.getUserByEmail(email)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error getting user by email: ${e.message}", e)
            null
        }
    }

    suspend fun insertUser(user: User) {
        try {
            storageManager.insertUser(user)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error inserting user: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    suspend fun updateUser(user: User) {
        try {
            storageManager.updateUser(user)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error updating user: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    suspend fun deleteUser(user: User) {
        try {
            storageManager.deleteUser(user)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error deleting user: ${e.message}", e)
            // Don't throw - allow app to continue
        }
    }

    fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val users = storageManager.getAllUsers()
            emit(users)
        } catch (e: Exception) {
            android.util.Log.e("UserDao", "Error getting all users: ${e.message}", e)
            emit(emptyList())
        }
    }
}
