package com.alcopoune.metertronik.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ElectricityRealtimeDto(
    val id: Long,
    @SerializedName("device_id") val deviceId: String,
    val voltage: Double,
    val current: Double,
    val power: Double,
    val energy: Double,
    @SerializedName("power_factor") val powerFactor: Double,
    val frequency: Double,
    @SerializedName("power_surge") val powerSurge: Double,
    @SerializedName("power_surge_percentage") val powerSurgePercentage: Double,
    @SerializedName("created_at") val createdAt: String
)
