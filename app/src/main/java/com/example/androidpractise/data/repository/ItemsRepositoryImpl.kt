package com.example.androidpractise.data.repository

import com.example.androidpractise.data.mapper.toDomain
import com.example.androidpractise.data.remote.RickAndMortyApi
import com.example.androidpractise.data.remote.safeApiCall
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.model.ItemsPage
import com.example.androidpractise.domain.repository.ItemsRepository

class ItemsRepositoryImpl(
    private val api: RickAndMortyApi
) : ItemsRepository {

    override suspend fun getItems(
        filters: ItemFilters,
        name: String?,
        page: Int
    ): Result<ItemsPage> = safeApiCall {
        api.getCharacters(
            page = page,
            status = filters.status,
            species = filters.species.takeIf { it.isNotBlank() },
            gender = filters.gender,
            name = name?.takeIf { it.isNotBlank() }
        ).toDomain()
    }

    override suspend fun getItem(id: Int): Result<Item> = safeApiCall {
        api.getCharacter(id = id).toDomain()
    }
}