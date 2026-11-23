package com.example.androidpractise.filters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.usecase.ObserveFiltersUseCase
import com.example.androidpractise.domain.usecase.SaveFiltersUseCase
import com.example.androidpractise.filters.FiltersBadgeCache
import com.example.androidpractise.filters.mvi.FiltersEffect
import com.example.androidpractise.filters.mvi.FiltersIntent
import com.example.androidpractise.filters.mvi.FiltersState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FiltersViewModel(
    private val observeFiltersUseCase: ObserveFiltersUseCase,
    private val saveFiltersUseCase: SaveFiltersUseCase,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModel() {

    private val _state = MutableStateFlow(FiltersState())
    val state: StateFlow<FiltersState> = _state.asStateFlow()

    private val _effects = Channel<FiltersEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val saved = observeFiltersUseCase().first()
            _state.update { it.copy(draft = saved, isLoading = false) }
        }
    }

    fun handleIntent(intent: FiltersIntent) {
        when (intent) {
            is FiltersIntent.StatusChanged -> updateDraft { it.copy(status = intent.status) }

            is FiltersIntent.SpeciesChanged -> updateDraft { it.copy(species = intent.species) }

            is FiltersIntent.GenderChanged -> updateDraft { it.copy(gender = intent.gender) }

            FiltersIntent.Reset -> updateDraft { ItemFilters.Default }

            FiltersIntent.Apply -> apply()
        }
    }

    private fun updateDraft(transform: (ItemFilters) -> ItemFilters) {
        _state.update { it.copy(draft = transform(it.draft)) }
    }

    private fun apply() {
        viewModelScope.launch {
            val filters = _state.value.draft
            saveFiltersUseCase(filters)
            filtersBadgeCache.setActive(!filters.isDefault)
            _effects.send(FiltersEffect.Close)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as AndroidPractiseApplication
                FiltersViewModel(
                    observeFiltersUseCase = app.container.observeFiltersUseCase,
                    saveFiltersUseCase = app.container.saveFiltersUseCase,
                    filtersBadgeCache = app.container.filtersBadgeCache
                )
            }
        }
    }
}
