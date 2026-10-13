package com.example.androidpractise.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.mvi.DetailsState
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.error.userMessage
import com.example.androidpractise.domain.usecase.GetItemUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getItemUseCase: GetItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadItem -> loadItem(intent.itemId)
        }
    }

    private fun loadItem(itemId: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getItemUseCase(id = itemId)
                .onSuccess { item ->
                    _state.update { it.copy(item = item, isLoading = false, error = null) }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isLoading = false, error = throwable.userMessage)
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as AndroidPractiseApplication
                DetailsViewModel(getItemUseCase = app.container.getItemUseCase)
            }
        }
    }
}