package com.example.dms.models

data class LoginResponse(
    val user: AuthUser?,
    val token: String?
)

data class AuthUser(
    val id: Int,
    val name: String,
    val email: String,
    val role: String?
)
