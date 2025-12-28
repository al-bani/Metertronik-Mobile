package com.alcopoune.metertronik.domain.model

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val status: String,
    val verified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class LoginResult(
    val user: UserData,
    val accessToken: String,
    val refreshToken: String,
    val message: String
)

