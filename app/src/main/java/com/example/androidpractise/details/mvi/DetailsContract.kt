package com.example.androidpractise.details.mvi

<<<<<<< HEAD
import com.example.androidpractise.domain.model.Item

data class DetailsState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val error: String? = null
=======
<<<<<<< HEAD
import com.example.androidpractise.details.model.DetailsUiModel

data class DetailsState(
    val item: DetailsUiModel? = null,
    val isLoading: Boolean = false
=======
import com.example.androidpractise.domain.model.Item

data class DetailsState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val error: String? = null
>>>>>>> 932a0dc (Practise2)
>>>>>>> 564097e (conflict resolution)
)

sealed interface DetailsIntent {
    data class LoadItem(val itemId: Int) : DetailsIntent
}
