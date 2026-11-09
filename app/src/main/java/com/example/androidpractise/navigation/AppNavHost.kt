package com.example.androidpractise.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.androidpractise.profile.viewmodel.ProfileViewModel
import com.example.androidpractise.profile.ui.ProfileRoute
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidpractise.details.ui.DetailsRoute
import com.example.androidpractise.details.viewmodel.DetailsViewModel
import com.example.androidpractise.favorites.ui.FavoritesRoute
import com.example.androidpractise.favorites.viewmodel.FavoritesViewModel
import com.example.androidpractise.filters.ui.FiltersRoute
import com.example.androidpractise.filters.viewmodel.FiltersViewModel
import com.example.androidpractise.items.ui.ItemsRoute
import com.example.androidpractise.items.viewmodel.ItemsViewModel

object Routes {
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "profile/edit"
    const val ITEMS = "items"
    const val FILTERS = "filters"
    const val FAVORITES = "favorites"
    const val DETAILS = "details"
    const val ITEM_ID = "itemId"
    const val DETAILS_PATTERN = "$DETAILS/{$ITEM_ID}"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val entry by navController.currentBackStackEntryAsState()
            val route = entry?.destination?.route
            if (route == Routes.ITEMS || route == Routes.PROFILE) {
                NavigationBar {
                    NavigationBarItem(
                        selected = route == Routes.ITEMS,
                        onClick = { navController.popBackStack(Routes.ITEMS, false) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Персонажи") }
                    )
                    NavigationBarItem(
                        selected = route == Routes.PROFILE,
                        onClick = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Профиль") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
            navController = navController,
            startDestination = Routes.ITEMS
        ) {
            composable(route = Routes.PROFILE) {
                val vm: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory)
                ProfileRoute(vm, false,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.EDIT_PROFILE) })
            }
            composable(route = Routes.EDIT_PROFILE) {
                val vm: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory)
                ProfileRoute(vm, true,
                    onBack = { navController.popBackStack() })
            }
            composable(route = Routes.ITEMS) {
                val viewModel: ItemsViewModel = viewModel(factory = ItemsViewModel.Factory)
                ItemsRoute(
                    viewModel = viewModel,
                    onItemClick = { itemId ->
                        navController.navigate("${Routes.DETAILS}/$itemId")
                    },
                    onOpenFilters = { navController.navigate(Routes.FILTERS) },
                    onOpenFavorites = { navController.navigate(Routes.FAVORITES) }
                )
            }

            composable(route = Routes.FILTERS) {
                val viewModel: FiltersViewModel = viewModel(factory = FiltersViewModel.Factory)
                FiltersRoute(
                    viewModel = viewModel,
                    onClose = { navController.popBackStack() }
                )
            }

            composable(route = Routes.FAVORITES) {
                val viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)
                FavoritesRoute(
                    viewModel = viewModel,
                    onItemClick = { itemId ->
                        navController.navigate("${Routes.DETAILS}/$itemId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.DETAILS_PATTERN,
                arguments = listOf(
                    navArgument(Routes.ITEM_ID) { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt(Routes.ITEM_ID) ?: 0
                val viewModel: DetailsViewModel = viewModel(factory = DetailsViewModel.Factory)
                DetailsRoute(
                    viewModel = viewModel,
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
