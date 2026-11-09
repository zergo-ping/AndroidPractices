package com.example.androidpractise.details.mvi

import com.example.androidpractise.details.model.DetailsUiModel

data class DetailsState(
    val item: DetailsUiModel? = null,
    val isLoading: Boolean = false
)

sealed interface DetailsIntent {
    data class LoadItem(val itemId: Int) : DetailsIntent
}

sealed interface DetailsEffect {
    data object NavigateBack : DetailsEffect
}
