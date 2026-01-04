package com.alcopoune.metertronik.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.presentation.screen.auth.login.LoginScreen
import com.alcopoune.metertronik.presentation.screen.auth.login.LoginViewModel
import com.alcopoune.metertronik.presentation.screen.auth.pairing.PairingScreen
import com.alcopoune.metertronik.presentation.screen.auth.register.RegisterScreen
import com.alcopoune.metertronik.presentation.screen.auth.verify.VerifyScreen
import com.alcopoune.metertronik.presentation.screen.main.dashboard.DashboardScreen
import com.alcopoune.metertronik.presentation.screen.main.list_data.ListDataScreen
import com.alcopoune.metertronik.presentation.screen.main.settings.SettingsScreen
import com.alcopoune.metertronik.presentation.screen.main.daily_detail.DetailsScreen
import com.alcopoune.metertronik.presentation.screen.error.ErrorScreen
import com.alcopoune.metertronik.presentation.screen.main.settings.SettingsViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    dataStorage: DataStorage
) {
    var initialDestination by remember { mutableStateOf<String?>(null) }
    
    // Check token on startup
    LaunchedEffect(Unit) {
        val refreshToken = dataStorage.getRefreshToken()
        val deviceId = dataStorage.getDeviceId()
        initialDestination = if (refreshToken.isNullOrBlank()) {
            Routes.Login.route
        } else if (deviceId.isNullOrBlank()) {
            Routes.Pairing.route
        } else {
            startDestination
        }
    }
    
    // Only show NavHost when initial destination is determined
    initialDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination
        ) {
        composable(Routes.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Routes.Pairing.route) {
            PairingScreen(navController = navController)
        }

        composable(Routes.Verify.route) {
            val prev = navController.previousBackStackEntry
            val email = prev?.savedStateHandle?.get<String>("verify_email").orEmpty()
            val source = prev?.savedStateHandle?.get<String>("verify_source").orEmpty()
            val autoResend = prev?.savedStateHandle?.get<Boolean>("verify_auto_resend") == true

            VerifyScreen(
                email = email,
                autoResendOtp = autoResend,
                onBack = {
                    // Dari Register: balik ke Register dan clear password UI-nya (password tidak disimpan).
                    if (source == "register") {
                        prev?.savedStateHandle?.set("clear_password", true)
                    }
                    prev?.savedStateHandle?.remove<String>("verify_email")
                    prev?.savedStateHandle?.remove<Boolean>("verify_auto_resend")
                    prev?.savedStateHandle?.remove<String>("verify_source")
                    navController.popBackStack()
                },
                onNavigateLogin = {
                    // Verify sukses -> arahkan user ke Login (tanpa auto-login), prefill email.
                    prev?.savedStateHandle?.set("prefill_email", email)
                    prev?.savedStateHandle?.remove<String>("verify_email")
                    prev?.savedStateHandle?.remove<Boolean>("verify_auto_resend")
                    prev?.savedStateHandle?.remove<String>("verify_source")

                    if (source == "login") {
                        // Balik ke Login yang ada di stack
                        navController.popBackStack()
                    } else {
                        // Dari Register -> pindah ke Login, buang Register & Verify dari stack
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Register.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Routes.ListData.route) {
            ListDataScreen(navController = navController)
        }

        composable(Routes.Setting.route) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = Routes.Detail.route,
            arguments = listOf(
                navArgument(Routes.Detail.ARG_DATE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString(Routes.Detail.ARG_DATE).orEmpty()
            DetailsScreen(navController = navController, date = date)
        }

        composable(
            route = Routes.Error.route,
            arguments = listOf(
                navArgument(Routes.Error.ARG_MESSAGE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString(Routes.Error.ARG_MESSAGE)
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                ?: "Terjadi kesalahan. Silakan coba lagi."
            
            ErrorScreen(
                errorMessage = message,
                onRetry = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        }
    }
}
