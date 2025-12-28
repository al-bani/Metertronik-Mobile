package com.alcopoune.metertronik.data.util

import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.remote.api.AuthApi
import com.alcopoune.metertronik.data.remote.dto.request.RefreshTokenRequest
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStorage: DataStorage,
    private val gson: Gson
) : Interceptor {

    companion object {
        private const val BASE_URL = "http://192.168.1.4:8080/v1/api/"
    }

    @Volatile
    private var isRefreshing = false
    private val refreshLock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token for auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/refresh")
        ) {
            return chain.proceed(originalRequest)
        }

        // Get access token
        val accessToken = runBlocking {
            dataStorage.getAccessToken()
        }

        // Add Authorization header if token exists
        val requestBuilder = originalRequest.newBuilder()
        if (!accessToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer testing")
        }

        var response = chain.proceed(requestBuilder.build())

        // Handle 401 Unauthorized (token expired)
        if (response.code == 401) {
            // Check if error message is "token expired"
            val responseBody = response.body?.string() ?: ""
            if (responseBody.contains("\"error\":\"token expired\"") ||
                responseBody.contains("\"error\": \"token expired\"")
            ) {
                response.close()

                // Refresh token
                val newAccessToken = synchronized(refreshLock) {
                    if (isRefreshing) {
                        // Wait for ongoing refresh
                        return@synchronized null
                    }
                    isRefreshing = true
                    try {
                        refreshToken()
                    } finally {
                        isRefreshing = false
                    }
                }

                if (newAccessToken != null) {
                    // Retry original request with new token
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    response = chain.proceed(retryRequest)
                } else {
                    // Refresh failed, return original 401 response
                    val errorBody = responseBody.toResponseBody("application/json".toMediaTypeOrNull())
                    response = Response.Builder()
                        .request(originalRequest)
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("Unauthorized")
                        .body(errorBody)
                        .build()
                }
            }
        }

        return response
    }

    private fun refreshToken(): String? {
        return runBlocking {
            try {
                val userId = dataStorage.getUserId()
                val refreshToken = dataStorage.getRefreshToken()

                if (userId == null || refreshToken.isNullOrBlank()) {
                    return@runBlocking null
                }

                // Create Retrofit client without interceptor for refresh token call
                val authClient = OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val authRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(authClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                val authApi = authRetrofit.create(AuthApi::class.java)

                val request = RefreshTokenRequest(
                    userId = userId,
                    refreshToken = refreshToken
                )

                val response = authApi.refreshToken(request)
                
                // Save new tokens
                dataStorage.saveTokens(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    userId = response.data.user.id
                )

                response.data.accessToken
            } catch (e: Exception) {
                e.printStackTrace()
                // If refresh fails, clear tokens
                dataStorage.clearTokens()
                null
            }
        }
    }
}

