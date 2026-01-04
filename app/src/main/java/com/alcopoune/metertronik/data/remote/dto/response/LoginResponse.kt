package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val data: LoginData,
    val message: String
)

data class LoginData(
    val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("is_paired") val userPaired: Boolean,
)

data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    val role: String,
    val status: String,
    val verified: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)