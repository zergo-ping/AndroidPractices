package com.example.androidpractise.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("status") val status: String,
    @SerialName("species") val species: String,
    @SerialName("type") val type: String = "",
    @SerialName("gender") val gender: String,
    @SerialName("origin") val origin: ReferenceDto,
    @SerialName("location") val location: ReferenceDto,
    @SerialName("image") val image: String,
    @SerialName("episode") val episode: List<String> = emptyList(),
    @SerialName("url") val url: String,
    @SerialName("created") val created: String
)

@Serializable
data class ReferenceDto(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String = ""
)

@Serializable
data class CharactersResponseDto(
    @SerialName("info") val info: PageInfoDto,
    @SerialName("results") val results: List<CharacterDto> = emptyList()
)

@Serializable
data class PageInfoDto(
    @SerialName("count") val count: Int,
    @SerialName("pages") val pages: Int,
    @SerialName("next") val next: String? = null,
    @SerialName("prev") val prev: String? = null
)
