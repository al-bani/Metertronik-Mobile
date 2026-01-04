package com.alcopoune.metertronik.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class DataStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    }

    private val dataStore = context.dataStore

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val userId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val deviceId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[DEVICE_ID_KEY]
    }

    suspend fun getAccessToken(): String? {
        return accessToken.first()
    }

    /**
     * Synchronous function to get access token for use in interceptors.
     * This is necessary because interceptors run on OkHttp threads and cannot use suspend functions.
     */
    fun getAccessTokenSync(): String? {
        return runBlocking {
            accessToken.first()
        }
    }

    suspend fun getRefreshToken(): String? {
        return refreshToken.first()
    }

    suspend fun getUserId(): Int? {
        return userId.first()
    }

    suspend fun getDeviceId(): String? {
        return deviceId.first()
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String, userId: Int? = null) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            userId?.let { preferences[USER_ID_KEY] = it }
        }
    }

    suspend fun saveDeviceId(deviceId: String) {
        dataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = deviceId
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(DEVICE_ID_KEY)
        }
    }

    fun clearTokensBlocking() {
        runBlocking {
            clearTokens()
        }
    }
}