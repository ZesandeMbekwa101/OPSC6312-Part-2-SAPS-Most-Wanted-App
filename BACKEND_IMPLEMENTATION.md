# SAPS Most Wanted App - Backend Implementation

## Overview
This document describes the backend implementation for the SAPS Most Wanted App's login and registration functionality.

## Architecture
The backend follows the MVVM (Model-View-ViewModel) architecture pattern with the following components:

### 1. Data Layer
- **User Model**: Data class representing user information
- **UserDao**: Data Access Object for database operations
- **AppDatabase**: Room database configuration
- **AuthRepository**: Repository pattern for data access

### 2. Presentation Layer
- **AuthViewModel**: ViewModel handling authentication logic
- **MainActivity**: Login screen with backend integration
- **RegistrationActivity**: Registration screen with backend integration

### 3. Dependency Injection
- **Hilt**: Used for dependency injection
- **DatabaseModule**: Provides database dependencies
- **RepositoryModule**: Provides repository dependencies

## Features Implemented

### Login Functionality
- Username and password validation
- Secure password verification using SHA-256 hashing
- User authentication against local database
- Loading states and error handling
- Success/error feedback via Toast messages

### Registration Functionality
- Complete user registration with validation
- Form validation (required fields, email format, password length)
- Duplicate username/email checking
- Secure password hashing before storage
- Loading states and error handling
- Success/error feedback via Toast messages

## Security Features
- Password hashing using SHA-256
- Input validation and sanitization
- Duplicate user prevention
- Secure database storage

## Database Schema
```sql
CREATE TABLE users (
    username TEXT PRIMARY KEY,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);
```

## Dependencies Added
- Room Database (2.5.0)
- Hilt for Dependency Injection (2.44)
- Kotlin Coroutines for async operations

## Usage

### Login
1. User enters username and password
2. System validates input
3. Password is hashed and compared with stored hash
4. On success, user is redirected to HomeActivity
5. On failure, error message is displayed

### Registration
1. User fills all required fields
2. System validates input format and requirements
3. System checks for duplicate username/email
4. Password is hashed and stored
5. On success, user is redirected to login screen
6. On failure, error message is displayed

## File Structure
```
app/src/main/java/com/example/sapsmostwantedapp/
├── data/
│   ├── model/
│   │   └── User.kt
│   ├── dao/
│   │   └── UserDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   └── repository/
│       └── AuthRepository.kt
├── ui/
│   └── viewmodel/
│       ├── AuthViewModel.kt
│       └── AuthViewModelFactory.kt
├── di/
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── utils/
│   └── PasswordUtils.kt
├── MainActivity.kt
├── RegistrationActivity.kt
└── SAPSApplication.kt
```

## Testing
The implementation includes proper error handling and validation that can be tested:
- Input validation
- Authentication flow
- Registration flow
- Database operations
- Error scenarios

## Future Enhancements
- Add biometric authentication
- Implement session management
- Add password reset functionality
- Implement user profile management
- Add more robust password requirements
- Implement account lockout after failed attempts










