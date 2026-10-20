package com.example.androidpractise.data.remote

import com.example.androidpractise.data.dto.CharacterDto
import com.example.androidpractise.data.dto.CharactersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    /**
     * Список персонажей с настраиваемыми query-параметрами.
     * Параметры со значением `null` Retrofit пропускает.
     */
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("gender") gender: String? = null,
        @Query("name") name: String? = null
    ): CharactersResponseDto

    /** Конкретный персонаж: id передаётся path-параметром. */
    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto
}