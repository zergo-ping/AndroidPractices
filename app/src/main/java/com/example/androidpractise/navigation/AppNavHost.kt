package com.example.androidpractise.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidpractise.details.ui.DetailsRoute
import com.example.androidpractise.details.viewmodel.DetailsViewModel
import com.example.androidpractise.items.ui.ItemsRoute
import com.example.androidpractise.items.viewmodel.ItemsViewModel

object Routes {
    const val ITEMS = "items"
    const val DETAILS = "details"
    const val ITEM_ID = "itemId"
    const val DETAILS_PATTERN = "$DETAILS/{$ITEM_ID}"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.ITEMS
    ) {
        composable(route = Routes.ITEMS) {
            val viewModel: ItemsViewModel = viewModel()
            ItemsRoute(
                viewModel = viewModel,
                onItemClick = { itemId ->
                    navController.navigate("${Routes.DETAILS}/$itemId")
                }
            )
        }

        composable(
            route = Routes.DETAILS_PATTERN,
            arguments = listOf(
                navArgument(Routes.ITEM_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt(Routes.ITEM_ID) ?: 0
            val viewModel: DetailsViewModel = viewModel()
            DetailsRoute(
                viewModel = viewModel,
                itemId = itemId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
