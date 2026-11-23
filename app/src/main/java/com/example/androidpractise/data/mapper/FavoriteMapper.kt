package com.example.androidpractise.data.mapper

import com.example.androidpractise.data.local.FavoriteEntity
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.Reference

fun Item.toEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    originName = origin.name,
    originUrl = origin.url,
    locationName = location.name,
    locationUrl = location.url,
    image = image,
    episode = episode,
    url = url,
    created = created
)

fun FavoriteEntity.toDomain(): Item = Item(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = Reference(name = originName, url = originUrl),
    location = Reference(name = locationName, url = locationUrl),
    image = image,
    episode = episode,
    url = url,
    created = created
)
