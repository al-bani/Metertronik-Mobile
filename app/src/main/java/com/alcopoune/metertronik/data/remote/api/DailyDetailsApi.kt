package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.response.DailyDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DailyDetailsApi {
    @GET("daily/{deviceId}/detail")
    suspend fun getDailyDetail(
        @Path("deviceId") deviceId: String,
        @Query("date") date: String
    ): DailyDetailResponse
}

