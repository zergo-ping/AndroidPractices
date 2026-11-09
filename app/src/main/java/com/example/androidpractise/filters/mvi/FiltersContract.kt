package com.example.androidpractise.filters.mvi

import com.example.androidpractise.domain.model.ItemFilters

data class FiltersState(
    val draft: ItemFilters = ItemFilters.Default,
    val isLoading: Boolean = true
) {
    val isDefault: Boolean get() = draft.isDefault
}

sealed interface FiltersIntent {
    data class StatusChanged(val status: String?) : FiltersIntent
    data class SpeciesChanged(val species: String) : FiltersIntent
    data class GenderChanged(val gender: String?) : FiltersIntent
    data object Reset : FiltersIntent
    data object Apply : FiltersIntent
}

sealed interface FiltersEffect {
    data object Close : FiltersEffect
}
