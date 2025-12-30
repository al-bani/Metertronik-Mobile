package com.alcopoune.metertronik.data.remote.dto.request

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)


