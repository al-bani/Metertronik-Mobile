package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.response.ListDataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ListDataApi {
    @GET("daily/{deviceId}")
    suspend fun getListData(
        @Path("deviceId") deviceId: String,
        @Query("last") last: String? = null
    ): ListDataResponse
}