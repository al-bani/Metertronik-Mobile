package com.alcopoune.metertronik.data.remote.dto.response

import com.alcopoune.metertronik.data.remote.dto.DailyDto
import com.alcopoune.metertronik.data.remote.dto.MonthlyDto

data class DashboardResponse(
    val data: DashboardDataWrapper?
)

data class DashboardDataWrapper(
    val month: MonthlyDto?,
    val daily: List<DailyDto>?,
    val monthly: List<MonthlyDto>?
)

