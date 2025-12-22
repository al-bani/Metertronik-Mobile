package com.alcopoune.metertronik.data.remote.dto.response

import com.alcopoune.metertronik.data.remote.dto.DailyDto
import com.alcopoune.metertronik.data.remote.dto.HourlyDto

data class DailyDetailResponse(
    val data: DailyDetailDataWrapper
)

data class DailyDetailDataWrapper(
    val daily: DailyDto,
    val hourly: List<HourlyDto>
)

