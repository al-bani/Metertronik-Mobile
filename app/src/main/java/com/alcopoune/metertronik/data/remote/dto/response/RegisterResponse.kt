package com.alcopoune.metertronik.data.remote.dto.response

data class RegisterResponse(
    val message: String,
    val data: RegisterData
)

data class RegisterData(
    val email: String,
    val username: String,
    val role: String,
    val status: Boolean
)


