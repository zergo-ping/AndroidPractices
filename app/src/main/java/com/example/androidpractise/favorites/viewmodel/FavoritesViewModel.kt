package com.example.androidpractise.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.usecase.ObserveFavoritesUseCase
import com.example.androidpractise.domain.usecase.RemoveFavoriteUseCase
import com.example.androidpractise.favorites.mvi.FavoritesEffect
import com.example.androidpractise.favorites.mvi.FavoritesIntent
import com.example.androidpractise.favorites.mvi.FavoritesState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val _effects = Channel<FavoritesEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { items ->
                _state.update { it.copy(items = items, isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.SelectItem -> sendEffect(
                FavoritesEffect.NavigateToDetails(intent.item.id)
            )

            is FavoritesIntent.RemoveFavorite -> remove(intent)
        }
    }

    private fun remove(intent: FavoritesIntent.RemoveFavorite) {
        viewModelScope.launch {
            removeFavoriteUseCase(intent.item.id)
            sendEffect(
                FavoritesEffect.ShowMessage("«${intent.item.name}» удалён из избранного")
            )
        }
    }

    private fun sendEffect(effect: FavoritesEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as AndroidPractiseApplication
                FavoritesViewModel(
                    observeFavoritesUseCase = app.container.observeFavoritesUseCase,
                    removeFavoriteUseCase = app.container.removeFavoriteUseCase
                )
            }
        }
    }
}
