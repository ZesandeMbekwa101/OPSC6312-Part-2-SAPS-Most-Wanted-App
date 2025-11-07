package com.example.sapsmostwantedapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    // Login method
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.authenticateUser(username, password)
                if (user != null) {
                    _currentUser.value = user // Set the logged-in user
                    Log.d("AuthViewModel", "User logged in: ${user.firstName} ${user.lastName}") // Add a log here
                    _loginResult.value = LoginResult.Success(user)
                } else {
                    _loginResult.value = LoginResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }



    // Logout method
    fun logout() {
        // Clear user session
        _currentUser.value = null
        // Optionally, you can clear tokens or any persistent data if applicable.
    }

    // Registration method
    fun register(firstName: String, lastName: String, username: String, password: String, email: String) {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank()) {
            _registrationResult.value = RegistrationResult.Error("Please fill in all fields")
            return
        }

        if (!isValidEmail(email)) {
            _registrationResult.value = RegistrationResult.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _registrationResult.value = RegistrationResult.Error("Password must be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = User(
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    isAdmin = false,
                    createdAt = System.currentTimeMillis()
                )

                val result = authRepository.registerUser(user)
                if (result.isSuccess) {
                    _registrationResult.value = RegistrationResult.Success("Registration successful!")
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                    Log.e("AuthViewModel", "Registration error: $errorMessage", result.exceptionOrNull())
                    _registrationResult.value = RegistrationResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration exception: ${e.message}", e)
                _registrationResult.value = RegistrationResult.Error("Registration failed: ${e.message ?: "Unknown error"}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Sealed classes for result states
    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    sealed class RegistrationResult {
        data class Success(val message: String) : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }
}

