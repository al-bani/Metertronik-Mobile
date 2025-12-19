package com.alcopoune.metertronik.data.remote.dto

data class DailyDetailResponse(
    val data: DataWrapper
)

data class DataWrapper(
    val daily: DailyDto,
    val hourly: List<HourlyDto>
)