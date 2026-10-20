package com.example.androidpractise.domain.model

/**
 * Набор фильтров списка персонажей. Значения уходят в query-параметры запроса.
 */
data class ItemFilters(
    val status: String? = null,
    val species: String = "",
    val gender: String? = null
) {
    val isDefault: Boolean
        get() = status == null && species.isBlank() && gender == null

    companion object {
        val Default = ItemFilters()
    }
}
