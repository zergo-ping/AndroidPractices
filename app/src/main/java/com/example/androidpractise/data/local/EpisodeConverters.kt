package com.example.androidpractise.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class EpisodeConverters {

    @TypeConverter
    fun fromEpisodeList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toEpisodeList(value: String): List<String> =
        runCatching { Json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())
}
