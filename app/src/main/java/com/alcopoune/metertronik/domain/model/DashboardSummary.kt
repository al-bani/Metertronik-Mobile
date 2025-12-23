package com.alcopoune.metertronik.domain.model

data class DashboardSummaryData (
    val daily: List<DailyData>,
    val monthly: MonthlyData,
    val monthlyList : List<MonthlyData>
)

