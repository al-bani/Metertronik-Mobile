package com.alcopoune.metertronik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("refresh_token") val refreshToken: String
)

