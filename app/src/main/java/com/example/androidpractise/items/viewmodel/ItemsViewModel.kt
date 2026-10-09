package com.example.androidpractise.items.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractise.items.data.ItemsRepository
import com.example.androidpractise.items.model.ItemsTab
import com.example.androidpractise.items.mvi.ItemsEffect
import com.example.androidpractise.items.mvi.ItemsIntent
import com.example.androidpractise.items.mvi.ItemsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val repository: ItemsRepository = ItemsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsState(isLoading = true))
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    private val _effects = Channel<ItemsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadItems(ItemsTab.ALL)
    }

    fun handleIntent(intent: ItemsIntent) {
        when (intent) {
            is ItemsIntent.SelectTab -> loadItems(intent.tab)
            is ItemsIntent.SelectItem -> sendEffect(
                ItemsEffect.NavigateToDetails(intent.item.id)
            )
        }
    }

    private fun loadItems(tab: ItemsTab) {
        _state.update { it.copy(selectedTab = tab, isLoading = true) }
        viewModelScope.launch {
            val items = repository.getItems(tab)
            _state.update { it.copy(items = items, isLoading = false) }
        }
    }

    private fun sendEffect(effect: ItemsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}