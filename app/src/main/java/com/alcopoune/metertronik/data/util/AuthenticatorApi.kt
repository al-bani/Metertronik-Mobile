package com.alcopoune.metertronik.data.util

import android.util.Log
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

/**
 * AuthenticatorApi menggunakan Provider<AuthRepository> untuk menghindari dependency cycle.
 * AuthRepository membutuhkan LogoutApi yang dibuat dari @Named("api") Retrofit,
 * yang membutuhkan @Named("api") OkHttpClient yang membutuhkan AuthenticatorApi.
 * Dengan menggunakan Provider, dependency di-resolve secara lazy.
 */
class AuthenticatorApi @Inject constructor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val dataStorage: DataStorage
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("jika 401", "Jika 401, teks ini muncul40")

        // ❗ Hindari infinite loop
        if (responseCount(response) >= 2) {
                dataStorage.clearTokensBlocking()
            return null
        }

        // ❗ Jangan refresh untuk endpoint auth
        if (
            response.request.url.encodedPath.contains("/auth/login") ||
            response.request.url.encodedPath.contains("/auth/refresh") ||
            response.request.url.encodedPath.contains("/auth/check-id") ||
            response.request.url.encodedPath.contains("/auth/register") ||
            response.request.url.encodedPath.contains("/auth/verify-otp") ||
            response.request.url.encodedPath.contains("/auth/resend-otp")
        ) {
            return null
        }

        synchronized(this) {
            // Kalau token sudah diperbarui oleh request lain
            val newToken = dataStorage.getAccessTokenSync()
            val oldToken = response.request.header("Authorization")

            if (newToken != null && oldToken != "Bearer $newToken") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }

            // 🔥 Refresh token (BLOCKING TAPI BENAR DI AUTHENTICATOR)
            // Menggunakan Provider.get() untuk mendapatkan AuthRepository secara lazy
            val success = runBlocking {
                authRepositoryProvider.get().refreshToken()
            }

            if (!success) {
                    dataStorage.clearTokensBlocking()
                return null
            }

            val refreshedToken = dataStorage.getAccessTokenSync()
                ?: return null

            return response.request.newBuilder()
                .header("Authorization", "Bearer $refreshedToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
