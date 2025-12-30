package com.alcopoune.metertronik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UserIdCheckRequest(
    @SerializedName("user_id") val userId: String
)


