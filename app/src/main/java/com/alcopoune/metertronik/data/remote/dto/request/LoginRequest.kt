package com.alcopoune.metertronik.data.remote.dto.request

import com.alcopoune.metertronik.data.remote.dto.DailyDto
import com.alcopoune.metertronik.data.remote.dto.HourlyDto

data class UserLoginRequest(
    val email: String = "",
    val username: String = "",
    val password: String
)

