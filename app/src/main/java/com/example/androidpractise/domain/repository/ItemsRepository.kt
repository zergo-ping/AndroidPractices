package com.example.androidpractise.domain.repository

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.model.ItemsPage

interface ItemsRepository {

    /**
     * Постраничная загрузка персонажей с фильтрами.
     *
     * @param filters значения query-параметров `status`, `species` и `gender`.
     * @param name значение query-параметра `name` для поиска по имени.
     * @param page номер страницы (query-параметр `page`).
     */
    suspend fun getItems(
        filters: ItemFilters,
        name: String?,
        page: Int
    ): Result<ItemsPage>

    /** Загрузка одного персонажа по id (path-параметр запроса). */
    suspend fun getItem(id: Int): Result<Item>
}