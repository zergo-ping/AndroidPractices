package com.example.androidpractise.domain.model

/**
 * Вкладки списка. Значение [status] уходит в query-параметр `status` запроса персонажей.
 */
enum class ItemsTab(val title: String, val status: String?) {
    ALL("Все", null),
    ALIVE("Живые", "Alive"),
    DEAD("Мёртвые", "Dead")
}
