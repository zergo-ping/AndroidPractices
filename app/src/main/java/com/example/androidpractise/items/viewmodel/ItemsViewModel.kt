package com.example.androidpractise.items.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.error.userMessage
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.usecase.GetItemsUseCase
import com.example.androidpractise.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.androidpractise.domain.usecase.ObserveFiltersUseCase
import com.example.androidpractise.domain.usecase.ToggleFavoriteUseCase
import com.example.androidpractise.filters.FiltersBadgeCache
import com.example.androidpractise.items.mvi.ItemsEffect
import com.example.androidpractise.items.mvi.ItemsIntent
import com.example.androidpractise.items.mvi.ItemsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val getItemsUseCase: GetItemsUseCase,
    private val observeFiltersUseCase: ObserveFiltersUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsState(isLoading = true))
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    private val _effects = Channel<ItemsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val isFiltersBadgeVisible: StateFlow<Boolean> = filtersBadgeCache.isActive

    private var loadJob: Job? = null
    private var loadedPage = 0

    init {
        observeFilters()
        observeFavorites()
    }

    fun handleIntent(intent: ItemsIntent) {
        when (intent) {
            is ItemsIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.query) }
                loadFirstPage(debounce = true)
            }

            ItemsIntent.LoadMore -> loadNextPage()

            ItemsIntent.Retry -> loadFirstPage()

            is ItemsIntent.SelectItem -> sendEffect(
                ItemsEffect.NavigateToDetails(intent.item.id)
            )

            is ItemsIntent.ToggleFavorite -> toggleFavorite(intent.item)

            ItemsIntent.OpenFilters -> sendEffect(ItemsEffect.NavigateToFilters)

            ItemsIntent.OpenFavorites -> sendEffect(ItemsEffect.NavigateToFavorites)
        }
    }

    private fun observeFilters() {
        viewModelScope.launch {
            observeFiltersUseCase()
                .distinctUntilChanged()
                .collect { filters ->
                    filtersBadgeCache.setActive(!filters.isDefault)
                    _state.update { it.copy(filters = filters) }
                    loadFirstPage()
                }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                _state.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    private fun toggleFavorite(item: Item) {
        viewModelScope.launch {
            val added = toggleFavoriteUseCase(item)
            val message = if (added) {
                "«${item.name}» добавлен в избранное"
            } else {
                "«${item.name}» удалён из избранного"
            }
            sendEffect(ItemsEffect.ShowMessage(message))
        }
    }

    private fun loadFirstPage(debounce: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, isLoadingMore = false, error = null)
            }
            if (debounce) delay(SEARCH_DEBOUNCE_MS)

            val current = _state.value
            getItemsUseCase(
                filters = current.filters,
                name = current.query,
                page = FIRST_PAGE
            )
                .onSuccess { page ->
                    loadedPage = FIRST_PAGE
                    _state.update {
                        it.copy(
                            items = page.items,
                            hasMore = page.hasMore,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    loadedPage = 0
                    _state.update {
                        it.copy(
                            items = emptyList(),
                            isLoading = false,
                            hasMore = false,
                            error = throwable.userMessage
                        )
                    }
                }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return

        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }

            getItemsUseCase(
                filters = current.filters,
                name = current.query,
                page = loadedPage + 1
            )
                .onSuccess { page ->
                    loadedPage += 1
                    _state.update {
                        it.copy(
                            items = it.items + page.items,
                            hasMore = page.hasMore,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isLoadingMore = false, error = throwable.userMessage)
                    }
                }
        }
    }

    private fun sendEffect(effect: ItemsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val SEARCH_DEBOUNCE_MS = 400L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as AndroidPractiseApplication
                ItemsViewModel(
                    getItemsUseCase = app.container.getItemsUseCase,
                    observeFiltersUseCase = app.container.observeFiltersUseCase,
                    observeFavoriteIdsUseCase = app.container.observeFavoriteIdsUseCase,
                    toggleFavoriteUseCase = app.container.toggleFavoriteUseCase,
                    filtersBadgeCache = app.container.filtersBadgeCache
                )
            }
        }
    }
}
