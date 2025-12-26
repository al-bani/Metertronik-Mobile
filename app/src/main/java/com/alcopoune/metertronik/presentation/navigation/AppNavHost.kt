package com.alcopoune.metertronik.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alcopoune.metertronik.presentation.screen.main.dashboard.DashboardScreen
import com.alcopoune.metertronik.presentation.screen.main.list_data.ListDataScreen
import com.alcopoune.metertronik.presentation.screen.main.settings.SettingsScreen
import com.alcopoune.metertronik.presentation.screen.main.daily_detail.DetailsScreen
import com.alcopoune.metertronik.presentation.screen.error.ErrorScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route
    ) {
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
