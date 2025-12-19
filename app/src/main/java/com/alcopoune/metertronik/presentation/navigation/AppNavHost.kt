package com.alcopoune.metertronik.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alcopoune.metertronik.presentation.screen.dashboard.DashboardScreen
import com.alcopoune.metertronik.presentation.screen.list_data.ListDataScreen
import com.alcopoune.metertronik.presentation.screen.settings.SettingsScreen
import com.alcopoune.metertronik.presentation.screen.daily_detail.DetailsScreen

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
                navArgument(Routes.Detail.ARG_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Routes.Detail.ARG_ID).orEmpty()
            DetailsScreen(navController = navController, id = id)
        }
    }
}
