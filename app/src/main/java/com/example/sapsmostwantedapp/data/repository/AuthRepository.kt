package com.example.sapsmostwantedapp.data.repository

import com.example.sapsmostwantedapp.data.dao.UserDao
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.utils.PasswordUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun authenticateUser(username: String, password: String): User? {
        return try {
            val user = userDao.getUserByUsername(username)
            android.util.Log.d("AuthRepository", "User lookup for '$username': ${if (user != null) "found" else "not found"}")
            if (user != null) {
                val passwordMatches = PasswordUtils.verifyPassword(password, user.password)
                android.util.Log.d("AuthRepository", "Password verification for '$username': $passwordMatches")
                android.util.Log.d("AuthRepository", "User isAdmin: ${user.isAdmin}")
                if (passwordMatches) {
                    user
                } else {
                    android.util.Log.w("AuthRepository", "Password mismatch for user: $username")
                    null
                }
            } else {
                android.util.Log.w("AuthRepository", "User not found: $username")
                null
            }
        } catch (e: Exception) {
            // Log the error for debugging
            android.util.Log.e("AuthRepository", "Error authenticating user: ${e.message}", e)
            null
        }
    }

    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getUserByUsername(user.username)
            if (existingUser != null) {
                return Result.failure(Exception("Username already exists"))
            }

            // Check if email already exists
            val existingEmail = userDao.getUserByEmail(user.email)
            if (existingEmail != null) {
                return Result.failure(Exception("Email already exists"))
            }

            // Hash the password before storing
            // Ensure all required fields are set, including isAdmin and createdAt
            val hashedUser = user.copy(
                password = PasswordUtils.hashPassword(user.password),
                isAdmin = user.isAdmin,
                createdAt = System.currentTimeMillis()
            )
            userDao.insertUser(hashedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            // Log the error for debugging
            android.util.Log.e("AuthRepository", "Error registering user: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
    
    suspend fun ensureAdminUserExists(): Boolean {
        return try {
            val adminUser = userDao.getUserByUsername("admin")
            val adminPassword = "admin123"
            val correctHashedPassword = PasswordUtils.hashPassword(adminPassword)
            
            android.util.Log.d("AuthRepository", "Checking admin user existence...")
            android.util.Log.d("AuthRepository", "Expected password hash: $correctHashedPassword")
            
            if (adminUser == null) {
                // Create admin user
                android.util.Log.d("AuthRepository", "Admin user not found, creating new one...")
                val admin = User(
                    username = "admin",
                    firstName = "System",
                    lastName = "Administrator",
                    email = "admin@saps.gov.za",
                    password = correctHashedPassword,
                    isAdmin = true,
                    createdAt = System.currentTimeMillis()
                )
                userDao.insertUser(admin)
                android.util.Log.d("AuthRepository", "Admin user created successfully")
                android.util.Log.d("AuthRepository", "Admin credentials - Username: admin, Password: admin123")
                
                // Verify it was created correctly
                val verifyUser = userDao.getUserByUsername("admin")
                if (verifyUser != null) {
                    val verifyPassword = PasswordUtils.verifyPassword(adminPassword, verifyUser.password)
                    android.util.Log.d("AuthRepository", "Verification - User exists: true, Password matches: $verifyPassword, isAdmin: ${verifyUser.isAdmin}")
                }
                true
            } else {
                android.util.Log.d("AuthRepository", "Admin user found, checking credentials...")
                android.util.Log.d("AuthRepository", "Stored password hash: ${adminUser.password}")
                android.util.Log.d("AuthRepository", "Current isAdmin: ${adminUser.isAdmin}")
                
                // Verify password is correct, if not, update it
                val passwordMatches = PasswordUtils.verifyPassword(adminPassword, adminUser.password)
                android.util.Log.d("AuthRepository", "Password verification result: $passwordMatches")
                
                val needsUpdate = !adminUser.isAdmin || !passwordMatches
                android.util.Log.d("AuthRepository", "Needs update: $needsUpdate (isAdmin: ${adminUser.isAdmin}, passwordMatch: $passwordMatches)")
                
                if (needsUpdate) {
                    android.util.Log.d("AuthRepository", "Updating admin user...")
                    val updatedAdmin = adminUser.copy(
                        isAdmin = true,
                        password = correctHashedPassword
                    )
                    userDao.updateUser(updatedAdmin)
                    android.util.Log.d("AuthRepository", "Admin user updated successfully")
                    android.util.Log.d("AuthRepository", "Admin credentials - Username: admin, Password: admin123")
                    
                    // Verify update
                    val verifyUser = userDao.getUserByUsername("admin")
                    if (verifyUser != null) {
                        val verifyPassword = PasswordUtils.verifyPassword(adminPassword, verifyUser.password)
                        android.util.Log.d("AuthRepository", "After update - Password matches: $verifyPassword, isAdmin: ${verifyUser.isAdmin}")
                    }
                } else {
                    android.util.Log.d("AuthRepository", "Admin user exists and is valid")
                }
                true
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Failed to ensure admin user exists: ${e.message}", e)
            e.printStackTrace()
            false
        }
    }
}
