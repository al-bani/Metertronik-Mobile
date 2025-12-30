package com.alcopoune.metertronik.data.util

import android.util.Log
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.remote.api.AuthApi
import com.alcopoune.metertronik.data.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * AuthInterceptor hanya bertugas menambahkan Authorization header jika token ada.
 * Tidak ada logika refresh token, error handling, atau retry di sini.
 * Semua logika bisnis (refresh token, error handling) ada di AuthRepository.
 */
class AuthInterceptor @Inject constructor(
    private val dataStorage: DataStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token for auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/refresh") ||
            originalRequest.url.encodedPath.contains("/auth/check-id") ||
            originalRequest.url.encodedPath.contains("/auth/register") ||
            originalRequest.url.encodedPath.contains("/auth/verify-otp") ||
            originalRequest.url.encodedPath.contains("/auth/resend-otp")
        ) {
            return chain.proceed(originalRequest)
        }

        // Get access token synchronously (for use in interceptor)
        val accessToken = dataStorage.getAccessTokenSync()

        Log.d("Intercept", "Intercept : getting Access Token")
        val requestBuilder = originalRequest.newBuilder()

        if (!accessToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}

