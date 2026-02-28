package com.seraphim.app.yxsg.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seraphim.app.yxsg.ui.calendar.CalendarScreen
import com.seraphim.app.yxsg.ui.checkin.CheckInScreen
import com.seraphim.app.yxsg.ui.settings.SettingsScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.CHECK_IN.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(BottomNavItem.CHECK_IN.route) {
                CheckInScreen(snackbarHostState = snackbarHostState)
            }
            composable(BottomNavItem.CALENDAR.route) {
                CalendarScreen(snackbarHostState = snackbarHostState)
            }
            composable(BottomNavItem.SETTINGS.route) {
                SettingsScreen(snackbarHostState = snackbarHostState)
            }
        }
    }
}
