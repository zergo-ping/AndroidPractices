package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.Flow

class ObserveFiltersUseCase(
    private val repository: FiltersRepository
) {
    operator fun invoke(): Flow<ItemFilters> = repository.observeFilters()
}

class SaveFiltersUseCase(
    private val repository: FiltersRepository
) {
    suspend operator fun invoke(filters: ItemFilters) = repository.saveFilters(filters)
}
