package com.alcopoune.metertronik.domain.model

data class MonthlyData(
    val deviceId: String,
    val energy: Double,
    val totalCost: Double,
    val month: String
)