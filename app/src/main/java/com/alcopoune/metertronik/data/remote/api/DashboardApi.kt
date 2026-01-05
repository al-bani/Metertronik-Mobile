package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.response.DashboardResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DashboardApi {
    @GET("monthly/{deviceId}")
    suspend fun getDashboard(
        @Path("deviceId") deviceId: String,
        @Query("date") date: String
    ): DashboardResponse?
}

