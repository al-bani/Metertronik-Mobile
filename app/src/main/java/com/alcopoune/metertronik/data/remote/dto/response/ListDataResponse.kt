package com.alcopoune.metertronik.data.remote.dto.response

import com.alcopoune.metertronik.data.remote.dto.DailyDto
import com.google.gson.annotations.SerializedName

data class ListDataResponse(
    val data: List<DailyDto>,
    val id: String,
    @SerializedName("last_date") val lastDate: String?,
    val message: String?
)