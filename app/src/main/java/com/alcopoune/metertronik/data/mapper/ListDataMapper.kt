package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.response.ListDataResponse
import com.alcopoune.metertronik.domain.model.ListDataSummary

fun ListDataResponse.toDomain(): ListDataSummary {
    return ListDataSummary(
        id = id,
        lastDate = lastDate,
        message = message.orEmpty(),
        daily = data.map { it.toDomain() },
    )
}