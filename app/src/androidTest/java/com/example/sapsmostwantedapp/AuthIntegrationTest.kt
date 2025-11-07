package com.example.sapsmostwantedapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sapsmostwantedapp.data.dao.UserDao
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.data.repository.AuthRepository
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.File

@RunWith(AndroidJUnit4::class)
class AuthIntegrationTest {

    private lateinit var storageManager: JsonStorageManager
    private lateinit var userDao: UserDao
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        storageManager = JsonStorageManager(context)
        userDao = UserDao(storageManager)
        authRepository = AuthRepository(userDao)
    }

    @After
    fun teardown() {
        // Clean up JSON files
        val context = ApplicationProvider.getApplicationContext<Context>()
        File(context.filesDir, "users.json").delete()
        File(context.filesDir, "reports.json").delete()
    }

    @Test
    fun testUserRegistration() = runBlocking {
        val user = User(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            password = "password123"
        )

        val result = authRepository.registerUser(user)
        assertTrue("User registration should succeed", result.isSuccess)
    }

    @Test
    fun testUserLogin() = runBlocking {
        val user = User(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            password = "password123"
        )

        // Register user first
        authRepository.registerUser(user)

        // Test login
        val loggedInUser = authRepository.authenticateUser("testuser", "password123")
        assertNotNull("User should be able to login", loggedInUser)
        assertEquals("Username should match", "testuser", loggedInUser?.username)
    }

    @Test
    fun testDuplicateUsernameRegistration() = runBlocking {
        val user1 = User(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "test1@example.com",
            password = "password123"
        )

        val user2 = User(
            username = "testuser",
            firstName = "Test2",
            lastName = "User2",
            email = "test2@example.com",
            password = "password456"
        )

        // Register first user
        authRepository.registerUser(user1)

        // Try to register second user with same username
        val result = authRepository.registerUser(user2)
        assertTrue("Registration should fail for duplicate username", result.isFailure)
    }

    @Test
    fun testDuplicateEmailRegistration() = runBlocking {
        val user1 = User(
            username = "testuser1",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            password = "password123"
        )

        val user2 = User(
            username = "testuser2",
            firstName = "Test2",
            lastName = "User2",
            email = "test@example.com",
            password = "password456"
        )

        // Register first user
        authRepository.registerUser(user1)

        // Try to register second user with same email
        val result = authRepository.registerUser(user2)
        assertTrue("Registration should fail for duplicate email", result.isFailure)
    }
}







