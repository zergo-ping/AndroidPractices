package com.example.androidpractise.details.mvi

import com.example.androidpractise.items.model.Item

data class DetailsState(
    val item: Item? = null,
    val isLoading: Boolean = false
)

sealed interface DetailsIntent {
    data class LoadItem(val itemId: Int) : DetailsIntent
}

sealed interface DetailsEffect {
    data object NavigateBack : DetailsEffect
}
