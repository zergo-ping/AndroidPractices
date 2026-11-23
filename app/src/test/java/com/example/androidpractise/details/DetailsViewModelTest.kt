package com.example.androidpractise.details

import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.viewmodel.DetailsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class DetailsViewModelTest {
    @Test
    fun loadItemProvidesDisplayReadyDetails() {
        val viewModel = DetailsViewModel()

        viewModel.handleIntent(DetailsIntent.LoadItem(1))

        val state = viewModel.state.value
        val item = state.item
        assertNotNull(item)
        assertFalse(state.isLoading)
        assertEquals("Rick Sanchez", item?.name)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", item?.image)
        assertEquals("Статус: Alive", item?.status)
        assertEquals("Вид: Human", item?.species)
        assertEquals("Тип: ", item?.type)
        assertEquals("Пол: Male", item?.gender)
        assertEquals("Происхождение: Earth (C-137)", item?.origin)
        assertEquals("Локация: Citadel of Ricks", item?.location)
        assertEquals("Создан: 2017-11-04T18:48:46.250Z", item?.created)
    }

    @Test
    fun loadingAnotherItemReplacesPreviousDetails() {
        val viewModel = DetailsViewModel()
        viewModel.handleIntent(DetailsIntent.LoadItem(1))

        viewModel.handleIntent(DetailsIntent.LoadItem(2))

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Morty Smith", state.item?.name)
        assertEquals("Происхождение: unknown", state.item?.origin)
        assertEquals("Создан: 2017-11-04T18:50:21.651Z", state.item?.created)
    }
}
