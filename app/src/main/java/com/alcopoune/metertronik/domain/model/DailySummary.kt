package com.alcopoune.metertronik.domain.model

data class DailyDetailsData(
    val daily: DailyData,
    val hourly: List<HourlyData>
)

