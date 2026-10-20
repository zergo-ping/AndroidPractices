package com.example.androidpractise.details.mvi

import com.example.androidpractise.domain.model.Item

data class DetailsState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val error: String? = null
)

sealed interface DetailsIntent {
    data class LoadItem(val itemId: Int) : DetailsIntent
    data object ToggleFavorite : DetailsIntent
}