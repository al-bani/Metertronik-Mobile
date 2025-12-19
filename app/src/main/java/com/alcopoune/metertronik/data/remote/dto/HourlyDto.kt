package com.alcopoune.metertronik.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HourlyDto(
    @SerializedName("device_id") val deviceId: String,
    val energy: Double,
    @SerializedName("total_cost") val totalCost: Double,
    @SerializedName("avg_voltage") val avgVoltage: Double,
    @SerializedName("avg_current") val avgCurrent: Double,
    @SerializedName("avg_power") val avgPower: Double,
    @SerializedName("min_power") val minPower: Double,
    @SerializedName("max_power") val maxPower: Double,
    val ts: String
)