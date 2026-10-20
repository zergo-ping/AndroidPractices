package com.example.androidpractise.data.mapper

import com.example.androidpractise.data.dto.CharacterDto
import com.example.androidpractise.data.dto.CharactersResponseDto
import com.example.androidpractise.data.dto.ReferenceDto
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemsPage
import com.example.androidpractise.domain.model.Reference

fun CharacterDto.toDomain(): Item = Item(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin.toDomain(),
    location = location.toDomain(),
    image = image,
    episode = episode,
    url = url,
    created = created
)

fun ReferenceDto.toDomain(): Reference = Reference(name = name, url = url)

fun CharactersResponseDto.toDomain(): ItemsPage = ItemsPage(
    items = results.map { it.toDomain() },
    hasMore = info.next != null
)
