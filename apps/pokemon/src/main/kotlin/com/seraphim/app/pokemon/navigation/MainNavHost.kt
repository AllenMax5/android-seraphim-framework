package com.seraphim.app.pokemon.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seraphim.app.pokemon.ui.detail.PokemonDetailScreen
import com.seraphim.app.pokemon.ui.favorites.FavoritesScreen
import com.seraphim.app.pokemon.ui.home.HomeScreen
import com.seraphim.app.pokemon.ui.types.TypesScreen

enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    POKEDEX("pokedex", Icons.Default.CatchingPokemon, "图鉴"),
    TYPES("types", Icons.Default.Category, "属性"),
    FAVORITES("favorites", Icons.Default.Favorite, "收藏"),
}

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = BottomNavItem.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.POKEDEX.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(BottomNavItem.POKEDEX.route) {
                HomeScreen(
                    onPokemonClick = { pokemonId ->
                        navController.navigate("detail/$pokemonId")
                    },
                )
            }
            composable(BottomNavItem.TYPES.route) {
                TypesScreen()
            }
            composable(BottomNavItem.FAVORITES.route) {
                FavoritesScreen(
                    onPokemonClick = { pokemonId ->
                        navController.navigate("detail/$pokemonId")
                    },
                )
            }
            composable(
                route = "detail/{pokemonId}",
                arguments = listOf(navArgument("pokemonId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: return@composable
                PokemonDetailScreen(
                    pokemonId = pokemonId,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
