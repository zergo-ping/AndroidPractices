package com.example.androidpractise.domain.repository

import com.example.androidpractise.domain.model.ItemFilters
import kotlinx.coroutines.flow.Flow

interface FiltersRepository {

    fun observeFilters(): Flow<ItemFilters>

    suspend fun saveFilters(filters: ItemFilters)
}
