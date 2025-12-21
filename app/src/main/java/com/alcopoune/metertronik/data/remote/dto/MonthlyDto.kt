package com.alcopoune.metertronik.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MonthlyDto(
    @SerializedName("device_id") val deviceId: String,
    val energy: Double,
    @SerializedName("total_cost") val totalCost: Double,
    val month: String
)