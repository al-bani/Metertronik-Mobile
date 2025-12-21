package com.alcopoune.metertronik.data.remote

import com.alcopoune.metertronik.data.remote.dto.DashboardResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DashboardApi{
    @GET("monthly/{deviceId}")
    suspend fun getDashboard(
        @Path("deviceId") deviceId: String,
        @Query("date") date: String
    ) : DashboardResponse
}