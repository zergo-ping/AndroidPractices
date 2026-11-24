package com.example.androidpractise.details

import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.viewmodel.DetailsViewModel
import com.example.androidpractise.domain.model.Item
import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.model.ItemsPage
import com.example.androidpractise.domain.model.Reference
import com.example.androidpractise.domain.repository.FavoritesRepository
import com.example.androidpractise.domain.repository.ItemsRepository
import com.example.androidpractise.domain.usecase.GetFavoriteUseCase
import com.example.androidpractise.domain.usecase.GetItemUseCase
import com.example.androidpractise.domain.usecase.ObserveIsFavoriteUseCase
import com.example.androidpractise.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val rick = Item(
        id = 1, name = "Rick Sanchez", status = "Alive", species = "Human", type = "",
        gender = "Male", origin = Reference("Earth (C-137)", ""),
        location = Reference("Citadel of Ricks", ""),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf("episode/1"), url = "", created = "2017-11-04T18:48:46.250Z"
    )
    private val morty = rick.copy(
        id = 2, name = "Morty Smith", origin = Reference("unknown", ""),
        created = "2017-11-04T18:50:21.651Z"
    )
    private val repository = object : ItemsRepository {
        override suspend fun getItem(id: Int): Result<Item> =
            runCatching { listOf(rick, morty).first { it.id == id } }

        override suspend fun getItems(filters: ItemFilters, name: String?, page: Int): Result<ItemsPage> =
            error("List requests are unexpected in details tests")
    }

    private val favorites = object : FavoritesRepository {
        private val items = MutableStateFlow<Map<Int, Item>>(emptyMap())

        override fun observeFavorites() = items.map { it.values.toList() }
        override fun observeFavoriteIds() = items.map { it.keys }
        override fun observeIsFavorite(id: Int) = items.map { id in it }
        override suspend fun getFavorite(id: Int) = items.value[id]

        override suspend fun add(item: Item) {
            items.value = items.value + (item.id to item)
        }

        override suspend fun remove(id: Int) {
            items.value = items.value - id
        }

        override suspend fun toggle(item: Item): Boolean {
            val adding = item.id !in items.value
            if (adding) add(item) else remove(item.id)
            return adding
        }
    }

    private fun createViewModel() = DetailsViewModel(
        GetItemUseCase(repository), GetFavoriteUseCase(favorites),
        ObserveIsFavoriteUseCase(favorites), ToggleFavoriteUseCase(favorites)
    )

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadItemProvidesDisplayReadyDetails() {
        val viewModel = createViewModel()

        viewModel.handleIntent(DetailsIntent.LoadItem(1))
        dispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        val item = state.item
        assertNotNull(item)
        assertFalse(state.isLoading)
        assertEquals("Rick Sanchez", item?.name)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", item?.image)
        assertEquals("Статус: Alive", item?.status)
        assertEquals("Вид: Human", item?.species)
        assertEquals("Тип: —", item?.type)
        assertEquals("Эпизодов: 1", item?.episodes)
        assertEquals("Пол: Male", item?.gender)
        assertEquals("Происхождение: Earth (C-137)", item?.origin)
        assertEquals("Локация: Citadel of Ricks", item?.location)
        assertEquals("Создан: 2017-11-04T18:48:46.250Z", item?.created)
    }

    @Test
    fun loadingAnotherItemReplacesPreviousDetails() {
        val viewModel = createViewModel()
        viewModel.handleIntent(DetailsIntent.LoadItem(1))
        dispatcher.scheduler.runCurrent()

        viewModel.handleIntent(DetailsIntent.LoadItem(2))
        dispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Morty Smith", state.item?.name)
        assertEquals("Происхождение: unknown", state.item?.origin)
        assertEquals("Создан: 2017-11-04T18:50:21.651Z", state.item?.created)
    }

    @Test
    fun offlineFavoriteUsesCachedDetailsAndCanBeRemoved() = runTest(dispatcher) {
        val cached = rick.copy(id = 3, name = "Cached character")
        favorites.add(cached)
        val viewModel = createViewModel()

        viewModel.handleIntent(DetailsIntent.LoadItem(cached.id))
        dispatcher.scheduler.runCurrent()
        assertEquals(cached.name, viewModel.state.value.item?.name)
        assertEquals("Статус: Alive", viewModel.state.value.item?.status)
        assertEquals(true, viewModel.state.value.isFavorite)
        assertEquals(null, viewModel.state.value.error)

        viewModel.handleIntent(DetailsIntent.ToggleFavorite)
        dispatcher.scheduler.runCurrent()
        assertFalse(viewModel.state.value.isFavorite)
    }
}
