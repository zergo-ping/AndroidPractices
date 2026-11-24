package com.example.androidpractise.details.model

import com.example.androidpractise.domain.model.Item

data class DetailsUiModel(
    val name: String,
    val image: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: String,
    val location: String,
    val episodes: String,
    val created: String
)

internal fun Item.toDetailsUiModel() = DetailsUiModel(
    name = name,
    image = image,
    status = "Статус: $status",
    species = "Вид: $species",
    type = "Тип: ${type.ifBlank { "—" }}",
    gender = "Пол: $gender",
    origin = "Происхождение: ${origin.name}",
    location = "Локация: ${location.name}",
    episodes = "Эпизодов: ${episode.size}",
    created = "Создан: $created"
)
