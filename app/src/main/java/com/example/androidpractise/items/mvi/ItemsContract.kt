package com.example.androidpractise.items.mvi

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemFilters

data class ItemsState(
    val query: String = "",
    val filters: ItemFilters = ItemFilters.Default,
    val items: List<Item> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val error: String? = null
)

sealed interface ItemsIntent {
    data class QueryChanged(val query: String) : ItemsIntent
    data object LoadMore : ItemsIntent
    data object Retry : ItemsIntent
    data class SelectItem(val item: Item) : ItemsIntent
    data class ToggleFavorite(val item: Item) : ItemsIntent
    data object OpenFilters : ItemsIntent
    data object OpenFavorites : ItemsIntent
}

sealed interface ItemsEffect {
    data class NavigateToDetails(val itemId: Int) : ItemsEffect
    data object NavigateToFilters : ItemsEffect
    data object NavigateToFavorites : ItemsEffect
}