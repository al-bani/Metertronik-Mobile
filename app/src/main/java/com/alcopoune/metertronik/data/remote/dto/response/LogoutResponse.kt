package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("status_logout") val statusLogout: Boolean,
    val message: String
)