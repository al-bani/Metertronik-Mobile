package com.alcopoune.metertronik

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.repository.AuthRepository
import com.alcopoune.metertronik.presentation.navigation.AppNavHost
import com.alcopoune.metertronik.presentation.navigation.Routes
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStorage: DataStorage

    @Inject
    lateinit var authRepository: AuthRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MAIN_ACTIVITY", "onCreate called")
        enableEdgeToEdge()

        val uri = intent?.data
        val status = uri?.getQueryParameter("status")

        val defaultDestination = when (status) {
            "success" -> "setting"
            "invalid" -> "email_failed"
            else -> "dashboard"
        }

        setContent {
            MetertronikTheme {
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val refreshToken = dataStorage.getRefreshToken()

                    if (refreshToken.isNullOrBlank()) {
                        startDestination = Routes.Login.route
                    } else {
                        val success = authRepository.refreshToken()
                        startDestination = if (success) {
                            defaultDestination
                        } else {
                            Routes.Login.route
                        }
                    }
                }

                startDestination?.let { destination ->
                    AppNavHost(
                        startDestination = destination,
                        dataStorage = dataStorage
                    )
                }
            }
        }
    }
}