package com.example.androidpractise.domain.repository

import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemsPage

interface ItemsRepository {

    /**
     * Постраничная загрузка персонажей с фильтрами.
     *
     * @param status значение query-параметра `status` (может отсутствовать).
     * @param name значение query-параметра `name` для поиска по имени.
     * @param page номер страницы (query-параметр `page`).
     */
    suspend fun getItems(
        status: String?,
        name: String?,
        page: Int
    ): Result<ItemsPage>

    /** Загрузка одного персонажа по id (path-параметр запроса). */
    suspend fun getItem(id: Int): Result<Item>
}
