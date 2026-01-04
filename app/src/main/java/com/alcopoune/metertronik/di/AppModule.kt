package com.alcopoune.metertronik.di

import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.remote.api.AuthApi
import com.alcopoune.metertronik.data.remote.api.DailyDetailsApi
import com.alcopoune.metertronik.data.remote.api.DashboardApi
import com.alcopoune.metertronik.data.remote.api.ListDataApi
import com.alcopoune.metertronik.data.remote.api.LogoutApi
import com.alcopoune.metertronik.data.remote.api.PairUserApi
import com.alcopoune.metertronik.data.remote.api.PairingStatusApi
import com.alcopoune.metertronik.data.remote.websocket.WebSocketService
import com.alcopoune.metertronik.data.repository.AuthRepository
import com.alcopoune.metertronik.data.repository.DailyDetailsRepository
import com.alcopoune.metertronik.data.repository.DashboardRepository
import com.alcopoune.metertronik.data.repository.ListDataRepository
import com.alcopoune.metertronik.data.repository.PairingRepository
import com.alcopoune.metertronik.data.repository.RealtimeRepository
import com.alcopoune.metertronik.data.util.AuthInterceptor
import com.alcopoune.metertronik.data.util.AuthenticatorApi
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
import javax.inject.Named
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
    @Named("auth")
    fun provideAuthOkHttpClient(): OkHttpClient {
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
    @Named("api")
    fun provideApiOkHttpClient(
        authInterceptor: AuthInterceptor,
        authenticatorApi: AuthenticatorApi
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .authenticator(authenticatorApi)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") client: OkHttpClient,
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
    @Named("api")
    fun provideApiRetrofit(
        @Named("api") client: OkHttpClient,
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
        @Named("api") retrofit: Retrofit
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
        @Named("api") retrofit: Retrofit
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
    fun provideListDataApi(
        @Named("api") retrofit: Retrofit
    ): ListDataApi {
        return  retrofit.create(ListDataApi::class.java)
    }

    @Provides
    @Singleton
    fun provideListDataRepository(
        api: ListDataApi
    ) : ListDataRepository {
        return ListDataRepository(api)
    }

    @Provides
    @Singleton
    fun provideWebSocketBaseUrl(): String {
        return WEBSOCKET_BASE_URL
    }

    @Provides
    @Singleton
    fun provideWebSocketService(
        @Named("api") okHttpClient: OkHttpClient,
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

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        dataStorage: DataStorage
    ): AuthInterceptor {
        return AuthInterceptor(dataStorage)
    }

    @Provides
    @Singleton
    fun provideAuthenticatorApi(
        authRepositoryProvider: javax.inject.Provider<AuthRepository>,
        dataStorage: DataStorage
    ): AuthenticatorApi {
        return AuthenticatorApi(authRepositoryProvider, dataStorage)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("auth") retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLogoutApi(
        @Named("api") retrofit: Retrofit
    ): LogoutApi {
        // Use "api" retrofit which includes AuthInterceptor and AuthenticatorApi
        // This ensures logout request includes Authorization header and can handle token refresh
        return retrofit.create(LogoutApi::class.java)
    }

    @Provides
    @Singleton
    fun providePairUserApi(
        @Named("api") retrofit: Retrofit
    ) : PairUserApi {
        return retrofit.create(PairUserApi::class.java)
    }

    @Provides
    @Singleton
    fun providePairingStatusApi(
        @Named("api") retrofit: Retrofit
    ): PairingStatusApi {
        return retrofit.create(PairingStatusApi::class.java)
    }

    @Provides
    @Singleton
    fun providePairingRepository(
        pairUserApi: PairUserApi,
        pairingStatusApi: PairingStatusApi
    ): PairingRepository {
        return PairingRepository(pairUserApi, pairingStatusApi)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        logoutApi: LogoutApi,
        dataStorage: DataStorage
    ): AuthRepository {
        return AuthRepository(api, logoutApi, dataStorage)
    }
}