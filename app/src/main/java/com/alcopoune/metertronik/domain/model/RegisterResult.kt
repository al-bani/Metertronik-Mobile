package com.alcopoune.metertronik.domain.model

data class RegisterResult(
    val email: String,
    val username: String,
    val role: String,
    val status: Boolean,
    val message: String
)


