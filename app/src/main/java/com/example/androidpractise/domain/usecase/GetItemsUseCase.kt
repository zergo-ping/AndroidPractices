package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.model.ItemsPage
import com.example.androidpractise.domain.repository.ItemsRepository

class GetItemsUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        filters: ItemFilters,
        name: String? = null,
        page: Int = 1
    ): Result<ItemsPage> = repository.getItems(filters = filters, name = name, page = page)
}