package com.alcopoune.metertronik.data.remote.dto

data class DashboardResponse(
    val data: DataWrapperMonth
)

data class DataWrapperMonth(
    val month: MonthlyDto,
    val daily: List<DailyDto>,
    val monthly: List<MonthlyDto>
)