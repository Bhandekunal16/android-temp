package com.example.myapp

data class Password(
    val id: String,
    val app: String,
    val username: String,
    val password: String,
)

data class Note(
    val id: String,
    val title: String,
    val content: String,
)

data class AuthRequest(
    val username: String,
)

data class ApiResponse<T>(
    val auth: Map<String, Any>?,
    val status: Boolean,
    val statusCode: Int,
    val message: String,
)
