package com.example.androidpractise.favorites.mvi

import com.example.androidpractise.domain.model.Item

data class FavoritesState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface FavoritesIntent {
    data class SelectItem(val item: Item) : FavoritesIntent
    data class RemoveFavorite(val item: Item) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class NavigateToDetails(val itemId: Int) : FavoritesEffect
    data class ShowMessage(val message: String) : FavoritesEffect
}