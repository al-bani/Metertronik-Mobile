package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    val data: RefreshTokenData,
    val message: String
)

data class RefreshTokenData(
    val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)

