package com.example.androidpractise.domain.repository

import com.example.androidpractise.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun observeFavorites(): Flow<List<Item>>

    fun observeFavoriteIds(): Flow<Set<Int>>

    fun observeIsFavorite(id: Int): Flow<Boolean>

    suspend fun getFavorite(id: Int): Item?

    suspend fun add(item: Item)

    suspend fun remove(id: Int)

    suspend fun toggle(item: Item): Boolean
}
