package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.response.DailyDetailResponse
import com.alcopoune.metertronik.data.remote.dto.DailyDto
import com.alcopoune.metertronik.data.remote.dto.HourlyDto
import com.alcopoune.metertronik.domain.model.DailyData
import com.alcopoune.metertronik.domain.model.DailyDetailsData
import com.alcopoune.metertronik.domain.model.HourlyData

fun DailyDetailResponse.toDomain(): DailyDetailsData {
    return DailyDetailsData(
        daily = data.daily.toDomain(),
        hourly = data.hourly.map { it.toDomain() }
    )
}

fun DailyDto.toDomain(): DailyData {
    return DailyData(
        deviceId = deviceId,
        energy = energy,
        totalCost = totalCost,
        avgVoltage = avgVoltage,
        avgCurrent = avgCurrent,
        avgPower = avgPower,
        minPower = minPower,
        maxPower = maxPower,
        day = day
    )
}

fun HourlyDto.toDomain(): HourlyData {
    return HourlyData(
        deviceId = deviceId,
        energy = energy,
        totalCost = totalCost,
        avgVoltage = avgVoltage,
        avgCurrent = avgCurrent,
        avgPower = avgPower,
        minPower = minPower,
        maxPower = maxPower,
        ts = ts
    )
}
