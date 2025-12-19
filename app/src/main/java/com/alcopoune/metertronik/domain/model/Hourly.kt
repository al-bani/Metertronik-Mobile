package com.alcopoune.metertronik.domain.model

data class HourlyData(
    val deviceId: String,
    val energy: Double,
    val totalCost: Double,
    val avgVoltage: Double,
    val avgCurrent: Double,
    val avgPower: Double,
    val minPower: Double,
    val maxPower: Double,
    val ts: String
)