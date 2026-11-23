package com.example.androidpractise.di

import android.app.Application
import androidx.room.Room
import com.example.androidpractise.data.local.AppDatabase
import com.example.androidpractise.data.local.filtersDataStore
import com.example.androidpractise.data.remote.NetworkModule
import com.example.androidpractise.data.repository.FavoritesRepositoryImpl
import com.example.androidpractise.data.repository.FiltersRepositoryImpl
import com.example.androidpractise.data.repository.ItemsRepositoryImpl
import com.example.androidpractise.domain.repository.FavoritesRepository
import com.example.androidpractise.domain.repository.FiltersRepository
import com.example.androidpractise.domain.repository.ItemsRepository
import com.example.androidpractise.domain.usecase.GetFavoriteUseCase
import com.example.androidpractise.domain.usecase.GetItemUseCase
import com.example.androidpractise.domain.usecase.GetItemsUseCase
import com.example.androidpractise.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.androidpractise.domain.usecase.ObserveFavoritesUseCase
import com.example.androidpractise.domain.usecase.ObserveFiltersUseCase
import com.example.androidpractise.domain.usecase.ObserveIsFavoriteUseCase
import com.example.androidpractise.domain.usecase.RemoveFavoriteUseCase
import com.example.androidpractise.domain.usecase.SaveFiltersUseCase
import com.example.androidpractise.domain.usecase.ToggleFavoriteUseCase
import com.example.androidpractise.filters.FiltersBadgeCache

/**
 * Ручной DI-контейнер: собирает граф зависимостей от слоя данных до use case'ов.
 */
class AppContainer(private val application: Application) {

    private val database: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        AppDatabase.NAME
    ).build()

    private val repository: ItemsRepository = ItemsRepositoryImpl(NetworkModule.api)

    private val filtersRepository: FiltersRepository =
        FiltersRepositoryImpl(application.filtersDataStore)

    private val favoritesRepository: FavoritesRepository =
        FavoritesRepositoryImpl(database.favoriteDao())

    val filtersBadgeCache: FiltersBadgeCache = FiltersBadgeCache()

    val getItemsUseCase: GetItemsUseCase = GetItemsUseCase(repository)

    val getItemUseCase: GetItemUseCase = GetItemUseCase(repository)

    val getFavoriteUseCase: GetFavoriteUseCase = GetFavoriteUseCase(favoritesRepository)

    val observeFiltersUseCase: ObserveFiltersUseCase = ObserveFiltersUseCase(filtersRepository)

    val saveFiltersUseCase: SaveFiltersUseCase = SaveFiltersUseCase(filtersRepository)

    val observeFavoritesUseCase: ObserveFavoritesUseCase =
        ObserveFavoritesUseCase(favoritesRepository)

    val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase =
        ObserveFavoriteIdsUseCase(favoritesRepository)

    val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase =
        ObserveIsFavoriteUseCase(favoritesRepository)

    val toggleFavoriteUseCase: ToggleFavoriteUseCase = ToggleFavoriteUseCase(favoritesRepository)

    val removeFavoriteUseCase: RemoveFavoriteUseCase = RemoveFavoriteUseCase(favoritesRepository)
}

class AndroidPractiseApplication : Application() {

    val container: AppContainer by lazy { AppContainer(this) }
}