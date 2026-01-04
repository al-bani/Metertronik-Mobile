package com.alcopoune.metertronik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UserPairingRequest (
    @SerializedName("device_id")
    val deviceId: String
)