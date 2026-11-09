package com.example.androidpractise.details.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidpractise.details.model.DetailsUiModel
import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.mvi.DetailsState
import com.example.androidpractise.items.data.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DetailsViewModel(
    private val repository: ItemsRepository = ItemsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadItem -> loadItem(intent.itemId)
        }
    }

    private fun loadItem(itemId: Int) {
        _state.update { it.copy(isLoading = true) }
        val item = repository.getItem(itemId)?.let { item ->
            DetailsUiModel(
                name = item.name,
                image = item.image,
                status = "Статус: ${item.status}",
                species = "Вид: ${item.species}",
                type = "Тип: ${item.type}",
                gender = "Пол: ${item.gender}",
                origin = "Происхождение: ${item.origin.name}",
                location = "Локация: ${item.location.name}",
                created = "Создан: ${item.created}"
            )
        }
        _state.update { it.copy(item = item, isLoading = false) }
    }
}
