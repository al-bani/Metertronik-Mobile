package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.response.LoginData
import com.alcopoune.metertronik.data.remote.dto.response.LoginResponse
import com.alcopoune.metertronik.data.remote.dto.response.UserDto
import com.alcopoune.metertronik.domain.model.LoginResult
import com.alcopoune.metertronik.domain.model.UserData

fun LoginResponse.toDomain(): LoginResult {
    return LoginResult(
        user = data.user.toDomain(),
        accessToken = data.accessToken,
        refreshToken = data.refreshToken,
        message = message
    )
}

fun UserDto.toDomain(): UserData {
    return UserData(
        id = id,
        username = username,
        email = email,
        role = role,
        status = status,
        verified = verified,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

