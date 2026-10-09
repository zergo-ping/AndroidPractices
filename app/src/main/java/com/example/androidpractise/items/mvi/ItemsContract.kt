package com.example.androidpractise.items.mvi

import com.example.androidpractise.items.model.Item
import com.example.androidpractise.items.model.ItemsTab

data class ItemsState(
    val selectedTab: ItemsTab = ItemsTab.ALL,
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface ItemsIntent {
    data class SelectTab(val tab: ItemsTab) : ItemsIntent
    data class SelectItem(val item: Item) : ItemsIntent
}

sealed interface ItemsEffect {
    data class NavigateToDetails(val itemId: Int) : ItemsEffect
}