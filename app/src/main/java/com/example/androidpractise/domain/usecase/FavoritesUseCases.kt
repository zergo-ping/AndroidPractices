package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Item>> = repository.observeFavorites()
}

class ObserveFavoriteIdsUseCase(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}

class ObserveIsFavoriteUseCase(
    private val repository: FavoritesRepository
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.observeIsFavorite(id)
}

class ToggleFavoriteUseCase(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(item: Item): Boolean = repository.toggle(item)
}

class RemoveFavoriteUseCase(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(id: Int) = repository.remove(id)
}

class GetFavoriteUseCase(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(id: Int): Item? = repository.getFavorite(id)
}
