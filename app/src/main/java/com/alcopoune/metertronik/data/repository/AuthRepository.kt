package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.AuthApi
import com.alcopoune.metertronik.data.remote.dto.request.RefreshTokenRequest
import com.alcopoune.metertronik.data.remote.dto.request.UserLoginRequest
import com.alcopoune.metertronik.data.util.ErrorHandler
import com.alcopoune.metertronik.domain.model.LoginResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
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

    suspend fun refreshToken(): Boolean {
        return try {
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