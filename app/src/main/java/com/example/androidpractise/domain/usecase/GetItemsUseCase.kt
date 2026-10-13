package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.ItemsPage
import com.example.androidpractise.domain.repository.ItemsRepository

class GetItemsUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        status: String?,
        name: String? = null,
        page: Int = 1
    ): Result<ItemsPage> = repository.getItems(status = status, name = name, page = page)
}
