package com.example.sapsmostwantedapp

import com.example.sapsmostwantedapp.utils.PasswordUtils
import org.junit.Test
import org.junit.Assert.*

class PasswordUtilsTest {

    @Test
    fun testPasswordHashing() {
        val password = "testPassword123"
        val hashedPassword = PasswordUtils.hashPassword(password)
        
        // Hash should not be empty
        assertTrue("Hashed password should not be empty", hashedPassword.isNotEmpty())
        
        // Hash should be different from original password
        assertNotEquals("Hashed password should be different from original", password, hashedPassword)
        
        // Same password should produce same hash
        val hashedPassword2 = PasswordUtils.hashPassword(password)
        assertEquals("Same password should produce same hash", hashedPassword, hashedPassword2)
    }

    @Test
    fun testPasswordVerification() {
        val password = "testPassword123"
        val hashedPassword = PasswordUtils.hashPassword(password)
        
        // Correct password should verify successfully
        assertTrue("Correct password should verify successfully", 
            PasswordUtils.verifyPassword(password, hashedPassword))
        
        // Wrong password should fail verification
        assertFalse("Wrong password should fail verification", 
            PasswordUtils.verifyPassword("wrongPassword", hashedPassword))
    }

    @Test
    fun testSaltGeneration() {
        val salt1 = PasswordUtils.generateSalt()
        val salt2 = PasswordUtils.generateSalt()
        
        // Salt should not be empty
        assertTrue("Salt should not be empty", salt1.isNotEmpty())
        
        // Different salts should be generated
        assertNotEquals("Different salts should be generated", salt1, salt2)
    }
}










