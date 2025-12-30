package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.request.LogoutRequest
import com.alcopoune.metertronik.data.remote.dto.response.LogoutResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Separate API interface for logout endpoint.
 * Uses @Named("api") retrofit which includes AuthInterceptor and AuthenticatorApi.
 * This ensures logout request includes Authorization header and can handle token refresh.
 */
interface LogoutApi {
    @POST("user/logout")
    suspend fun userLogout(
        @Body request: LogoutRequest
    ): LogoutResponse
}

