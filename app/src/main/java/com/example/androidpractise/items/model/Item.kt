package com.example.androidpractise.items.model

data class Reference(
    val name: String,
    val url: String
)

data class Item(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Reference,
    val location: Reference,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

enum class ItemsTab(val title: String, val status: String?) {
    ALL("Все", null),
    ALIVE("Живые", "Alive"),
    DEAD("Мёртвые", "Dead")
}