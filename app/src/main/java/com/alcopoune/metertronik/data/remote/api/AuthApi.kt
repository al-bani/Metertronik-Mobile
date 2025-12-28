package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.request.RefreshTokenRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserLoginRequest
import com.alcopoune.metertronik.data.remote.dto.response.LoginResponse
import com.alcopoune.metertronik.data.remote.dto.response.RefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun userLogin(
        @Body request: UserLoginRequest
    ): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): RefreshTokenResponse
}