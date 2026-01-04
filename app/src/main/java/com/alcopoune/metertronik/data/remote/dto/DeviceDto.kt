package com.alcopoune.metertronik.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeviceDto(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("device_secret") val deviceSecret: String,
    @SerializedName("is_paired") val isPaired: Boolean,
)