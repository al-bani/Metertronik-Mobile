package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.request.RefreshTokenRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserIdCheckRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserLoginRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserRegisterRequest
import com.alcopoune.metertronik.data.remote.dto.request.VerifyOtpRequest
import com.alcopoune.metertronik.data.remote.dto.request.ResendOtpRequest
import com.alcopoune.metertronik.data.remote.dto.response.CheckIdResponse
import com.alcopoune.metertronik.data.remote.dto.response.LoginResponse
import com.alcopoune.metertronik.data.remote.dto.response.RegisterResponse
import com.alcopoune.metertronik.data.remote.dto.response.RefreshTokenResponse
import com.alcopoune.metertronik.data.remote.dto.response.VerifyOtpResponse
import com.alcopoune.metertronik.data.remote.dto.response.ResendOtpResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("auth/login")
    suspend fun userLogin(
        @Body request: UserLoginRequest
    ): LoginResponse

    @POST("auth/register")
    suspend fun userRegister(
        @Body request: UserRegisterRequest
    ): RegisterResponse

    @POST("auth/check-id")
    suspend fun checkId(
        @Body request: UserIdCheckRequest
    ): CheckIdResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): VerifyOtpResponse

    @POST("auth/resend-otp")
    suspend fun resendOtp(
        @Body request: ResendOtpRequest
    ): ResendOtpResponse

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): RefreshTokenResponse


}