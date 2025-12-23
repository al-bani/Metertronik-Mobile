package com.alcopoune.metertronik.domain.model

data class ListDataSummary(
    val id: String,
    val lastDate: String?,
    val message: String,
    val daily: List<DailyData>,
)
