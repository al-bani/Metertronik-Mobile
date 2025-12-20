package com.alcopoune.metertronik.domain.model

data class ElectricityRealtime(
    val id: Long,
    val deviceId: String,
    val voltage: Double,
    val current: Double,
    val power: Double,
    val energy: Double,
    val powerFactor: Double,
    val frequency: Double,
    val powerSurge: Double,
    val powerSurgePercentage: Double,
    val createdAt: String
)