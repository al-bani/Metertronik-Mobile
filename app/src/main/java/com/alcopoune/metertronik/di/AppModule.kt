package com.alcopoune.metertronik.di

import com.alcopoune.metertronik.data.remote.api.DailyDetailsApi
import com.alcopoune.metertronik.data.remote.api.DashboardApi
import com.alcopoune.metertronik.data.remote.websocket.WebSocketService
import com.alcopoune.metertronik.data.repository.DailyDetailsRepository
import com.alcopoune.metertronik.data.repository.DashboardRepository
import com.alcopoune.metertronik.data.repository.RealtimeRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://192.168.1.4:8080/v1/api/"
    private const val WEBSOCKET_BASE_URL = "ws://192.168.1.4:8080/v1/ws/electricity/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideDailyDetailsApi(
        retrofit: Retrofit
    ): DailyDetailsApi {
        return retrofit.create(DailyDetailsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDailyDetailsRepository(
        api: DailyDetailsApi
    ): DailyDetailsRepository {
        return DailyDetailsRepository(api)
    }

    @Provides
    @Singleton
    fun provideDashboardApi(
        retrofit: Retrofit
    ): DashboardApi {
        return  retrofit.create(DashboardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        api: DashboardApi
    ) : DashboardRepository {
        return DashboardRepository(api)
    }

    @Provides
    @Singleton
    fun provideWebSocketBaseUrl(): String {
        return WEBSOCKET_BASE_URL
    }

    @Provides
    @Singleton
    fun provideWebSocketService(
        okHttpClient: OkHttpClient,
        gson: Gson,
        webSocketBaseUrl: String
    ): WebSocketService {
        return WebSocketService(okHttpClient, gson, webSocketBaseUrl)
    }

    @Provides
    @Singleton
    fun provideRealtimeRepository(
        webSocketService: WebSocketService
    ): RealtimeRepository {
        return RealtimeRepository(webSocketService)
    }
}