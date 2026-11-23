package com.example.androidpractise.items.mvi

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemsTab

data class ItemsState(
    val selectedTab: ItemsTab = ItemsTab.ALL,
    val query: String = "",
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val error: String? = null
)

sealed interface ItemsIntent {
    data class SelectTab(val tab: ItemsTab) : ItemsIntent
    data class QueryChanged(val query: String) : ItemsIntent
    data object LoadMore : ItemsIntent
    data object Retry : ItemsIntent
    data class SelectItem(val item: Item) : ItemsIntent
}

sealed interface ItemsEffect {
    data class NavigateToDetails(val itemId: Int) : ItemsEffect
}
