package com.alcopoune.metertronik.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alcopoune.metertronik.presentation.navigation.AppDestination
import com.alcopoune.metertronik.presentation.navigation.MainNavGraph

data class BottomBarItem(
    val destination: AppDestination,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomBarItem(
            destination = AppDestination.ListData,
            label = "List Data",
            icon = { Icon(Icons.Default.List, contentDescription = "List Data") }
        ),
        BottomBarItem(
            destination = AppDestination.Dashboard,
            label = "Dashboard",
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") }
        ),
        BottomBarItem(
            destination = AppDestination.Settings,
            label = "Settings",
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
        )
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                bottomItems.forEach { item ->
                    val selected = currentDestination.isDestinationInHierarchy(item.destination)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.destination.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = item.icon,
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        MainNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private fun NavDestination?.isDestinationInHierarchy(destination: AppDestination): Boolean {
    return this?.hierarchy?.any { it.route == destination.route } == true
}


