package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.response.ListDataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ListDataApi {
    @GET("daily/{deviceId}")
    suspend fun getListData(
        @Path("deviceId") deviceId: String,
        @Query("time") time : String? = null,
        @Query("tariff") tariff: String? = null,
        @Query("last") last: String? = null
    ): ListDataResponse

    @GET("daily/{deviceId}/range")
    suspend fun getRangeListData(
        @Path("deviceId") deviceId: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("last") last: String? = null
    ) : ListDataResponse
}