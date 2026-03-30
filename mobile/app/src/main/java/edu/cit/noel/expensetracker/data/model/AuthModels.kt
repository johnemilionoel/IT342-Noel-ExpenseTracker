package edu.cit.noel.expensetracker.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val message: String
)
