package com.example.androidpractise.di

import android.app.Application
import com.example.androidpractise.data.remote.NetworkModule
import com.example.androidpractise.data.repository.ItemsRepositoryImpl
import com.example.androidpractise.domain.repository.ItemsRepository
import com.example.androidpractise.domain.usecase.GetItemUseCase
import com.example.androidpractise.domain.usecase.GetItemsUseCase

/**
 * Ручной DI-контейнер: собирает граф зависимостей от слоя данных до use case'ов.
 */
class AppContainer {

    private val repository: ItemsRepository = ItemsRepositoryImpl(NetworkModule.api)

    val getItemsUseCase: GetItemsUseCase = GetItemsUseCase(repository)

    val getItemUseCase: GetItemUseCase = GetItemUseCase(repository)
}

class AndroidPractiseApplication : Application() {

    val container: AppContainer by lazy { AppContainer() }
}
