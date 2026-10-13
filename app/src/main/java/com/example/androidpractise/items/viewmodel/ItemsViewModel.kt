package com.example.androidpractise.items.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.error.userMessage
import com.example.androidpractise.domain.usecase.GetItemsUseCase
import com.example.androidpractise.items.mvi.ItemsEffect
import com.example.androidpractise.items.mvi.ItemsIntent
import com.example.androidpractise.items.mvi.ItemsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsState(isLoading = true))
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    private val _effects = Channel<ItemsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var loadJob: Job? = null
    private var loadedPage = 0

    init {
        loadFirstPage()
    }

    fun handleIntent(intent: ItemsIntent) {
        when (intent) {
            is ItemsIntent.SelectTab -> {
                _state.update { it.copy(selectedTab = intent.tab) }
                loadFirstPage()
            }

            is ItemsIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.query) }
                loadFirstPage(debounce = true)
            }

            ItemsIntent.LoadMore -> loadNextPage()

            ItemsIntent.Retry -> loadFirstPage()

            is ItemsIntent.SelectItem -> sendEffect(
                ItemsEffect.NavigateToDetails(intent.item.id)
            )
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
                status = current.selectedTab.status,
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
                status = current.selectedTab.status,
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
                ItemsViewModel(getItemsUseCase = app.container.getItemsUseCase)
            }
        }
    }
}