package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.AuthApi
import com.alcopoune.metertronik.data.remote.api.LogoutApi
import com.alcopoune.metertronik.data.remote.dto.request.UserIdCheckRequest
import com.alcopoune.metertronik.data.remote.dto.request.LogoutRequest
import com.alcopoune.metertronik.data.remote.dto.request.ResendOtpRequest
import com.alcopoune.metertronik.data.remote.dto.request.RefreshTokenRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserLoginRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserRegisterRequest
import com.alcopoune.metertronik.data.remote.dto.request.VerifyOtpRequest
import com.alcopoune.metertronik.data.remote.dto.response.CheckIdResponse
import com.alcopoune.metertronik.data.remote.dto.response.ResendOtpResponse
import com.alcopoune.metertronik.data.remote.dto.response.VerifyOtpResponse
import com.alcopoune.metertronik.data.util.ErrorHandler
import com.alcopoune.metertronik.domain.model.LoginResult
import com.alcopoune.metertronik.domain.model.RegisterResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val logoutApi: LogoutApi,
    private val dataStorage: DataStorage
) {
    private val tag = "AuthRepository"

    suspend fun userLogin(
        request: UserLoginRequest
    ): LoginResult {
        return try {
            Log.d(tag, "userLogin() - calling API")
            val response = api.userLogin(request)
            Log.d(tag, "userLogin() success, mapping to domain")
            response.toDomain()
        } catch (e: Exception) {
            Log.e(tag, "Error during login", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun userRegister(
        request: UserRegisterRequest
    ): RegisterResult {
        return try {
            Log.d(tag, "userRegister() - calling API")
            val response = api.userRegister(request)
            Log.d(tag, "userRegister() success, mapping to domain")
            response.toDomain()
        } catch (e: Exception) {
            Log.e(tag, "Error during register", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun checkIdAvailable(userId: String): CheckIdResponse {
        return try {
            Log.d(tag, "checkIdAvailable() - calling API")
            api.checkId(UserIdCheckRequest(userId = userId))
        } catch (e: Exception) {
            Log.e(tag, "Error during check id", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): VerifyOtpResponse {
        return try {
            Log.d(tag, "verifyOtp() - calling API")
            api.verifyOtp(VerifyOtpRequest(email = email, otp = otp))
        } catch (e: Exception) {
            Log.e(tag, "Error during verify otp", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun resendOtp(email: String): ResendOtpResponse {
        return try {
            Log.d(tag, "resendOtp() - calling API")
            api.resendOtp(ResendOtpRequest(email = email))
        } catch (e: Exception) {
            Log.e(tag, "Error during resend otp", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun userLogout() : Boolean {
        return try {
            // Get refresh token from storage
            val refreshToken = dataStorage.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                Log.e(tag, "userLogout() - no refresh token found")
                return false
            }

            Log.d(tag, "userLogout() - calling LogoutApi with refresh token")
            val request = LogoutRequest(refreshToken = refreshToken)
            val response = logoutApi.userLogout(request)
            Log.d(tag, "userLogout() - response: statusLogout=${response.statusLogout}")

            if (response.statusLogout) {
                dataStorage.clearTokens()
                Log.d(tag, "userLogout() - tokens cleared")
                true
            } else {
                Log.e(tag, "userLogout() - logout failed: ${response.message}")
                false
            }
        } catch (e : Exception){
            Log.e(tag, "Error during Logout User", e)
            false
        }
    }

    suspend fun refreshToken(): Boolean {
        return try {
            Log.d("refreshToken112", "melakukan refresh tokan")
            val userId = dataStorage.getUserId()
            val refreshToken = dataStorage.getRefreshToken()

            if (userId == null || refreshToken.isNullOrBlank()) {
                Log.d(tag, "refreshToken() - no userId or refreshToken found")
                return false
            }

            Log.d(tag, "refreshToken() - calling API")
            val request = RefreshTokenRequest(
                userId = userId,
                refreshToken = refreshToken
            )

            val response = api.refreshToken(request)

            // Save new tokens
            dataStorage.saveTokens(
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken,
                userId = response.data.user.id
            )

            Log.d(tag, "refreshToken() - success, tokens saved")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error during refresh token", e)
            // If refresh fails, clear tokens
            dataStorage.clearTokens()
            false
        }
    }
}