package com.example.androidpractise.domain.model

/**
 * Страница списка персонажей: элементы и признак наличия следующей страницы.
 */
data class ItemsPage(
    val items: List<Item>,
    val hasMore: Boolean
)
