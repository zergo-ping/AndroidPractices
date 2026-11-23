package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.repository.ItemsRepository

class GetItemUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(id: Int): Result<Item> = repository.getItem(id)
}
