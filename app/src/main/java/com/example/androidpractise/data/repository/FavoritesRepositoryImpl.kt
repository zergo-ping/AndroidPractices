package com.example.androidpractise.data.repository

import com.example.androidpractise.data.local.FavoriteDao
import com.example.androidpractise.data.mapper.toDomain
import com.example.androidpractise.data.mapper.toEntity
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteDao
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<Item>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeIds().map { it.toSet() }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = dao.observeIsFavorite(id)

    override suspend fun getFavorite(id: Int): Item? = dao.getById(id)?.toDomain()

    override suspend fun add(item: Item) = dao.insert(item.toEntity())

    override suspend fun remove(id: Int) = dao.deleteById(id)

    override suspend fun toggle(item: Item): Boolean {
        val isFavorite = dao.isFavorite(item.id)
        if (isFavorite) {
            dao.deleteById(item.id)
        } else {
            dao.insert(item.toEntity())
        }
        return !isFavorite
    }
}
