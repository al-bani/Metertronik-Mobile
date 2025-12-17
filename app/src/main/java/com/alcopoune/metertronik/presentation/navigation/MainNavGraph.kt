package com.alcopoune.metertronik.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alcopoune.metertronik.presentation.screen.dashboard.DashboardScreen
import com.alcopoune.metertronik.presentation.screen.daily_details.DetailsCurrentDayScreen
import com.alcopoune.metertronik.presentation.screen.daily_details.DetailsHistoryDayScreen
import com.alcopoune.metertronik.presentation.screen.list_data.ListDataScreen
import com.alcopoune.metertronik.presentation.screen.settings.SettingsScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Dashboard.route,
        modifier = modifier
    ) {
        composable(AppDestination.ListData.route) {
            ListDataScreen(
                onNavigateToDetailsHistoryDay = {
                    navController.navigate(AppDestination.DetailsHistoryDay.route)
                },
                onNavigateToDetailsCurrentDay = {
                    navController.navigate(AppDestination.DetailsCurrentDay.route)
                }
            )
        }

        composable(AppDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateToDetailsHistoryDay = {
                    navController.navigate(AppDestination.DetailsHistoryDay.route)
                },
                onNavigateToDetailsCurrentDay = {
                    navController.navigate(AppDestination.DetailsCurrentDay.route)
                }
            )
        }

        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }

        composable(AppDestination.DetailsHistoryDay.route) {
            DetailsHistoryDayScreen()
        }

        composable(AppDestination.DetailsCurrentDay.route) {
            DetailsCurrentDayScreen()
        }
    }
}


