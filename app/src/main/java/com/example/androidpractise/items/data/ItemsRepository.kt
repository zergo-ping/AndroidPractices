package com.example.androidpractise.items.data

import com.example.androidpractise.items.model.Item
import com.example.androidpractise.items.model.ItemsTab
import com.example.androidpractise.items.model.Reference

class ItemsRepository {

    private val items = listOf(
        Item(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Reference("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
            location = Reference("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2",
                "https://rickandmortyapi.com/api/episode/3"
            ),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z"
        ),
        Item(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Reference("unknown", ""),
            location = Reference("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
            ),
            url = "https://rickandmortyapi.com/api/character/2",
            created = "2017-11-04T18:50:21.651Z"
        ),
        Item(
            id = 3,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Female",
            origin = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            location = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/6",
                "https://rickandmortyapi.com/api/episode/7"
            ),
            url = "https://rickandmortyapi.com/api/character/3",
            created = "2017-11-04T19:09:56.666Z"
        ),
        Item(
            id = 4,
            name = "Beth Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Female",
            origin = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            location = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/6",
                "https://rickandmortyapi.com/api/episode/7"
            ),
            url = "https://rickandmortyapi.com/api/character/4",
            created = "2017-11-04T19:22:43.665Z"
        ),
        Item(
            id = 5,
            name = "Jerry Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            location = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/5.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/6",
                "https://rickandmortyapi.com/api/episode/7"
            ),
            url = "https://rickandmortyapi.com/api/character/5",
            created = "2017-11-04T19:26:56.301Z"
        ),
        Item(
            id = 7,
            name = "Abradolf Lincler",
            status = "Dead",
            species = "Human",
            type = "Genetic experiment",
            gender = "Male",
            origin = Reference("Earth (Replacement Dimension)", "https://rickandmortyapi.com/api/location/20"),
            location = Reference("Testicle Monster Dimension", "https://rickandmortyapi.com/api/location/21"),
            image = "https://rickandmortyapi.com/api/character/avatar/7.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/10",
                "https://rickandmortyapi.com/api/episode/11"
            ),
            url = "https://rickandmortyapi.com/api/character/7",
            created = "2017-11-04T19:59:20.523Z"
        ),
        Item(
            id = 361,
            name = "Toxic Rick",
            status = "Dead",
            species = "Humanoid",
            type = "Rick's Toxic Side",
            gender = "Male",
            origin = Reference("Alien Spa", "https://rickandmortyapi.com/api/location/64"),
            location = Reference("Earth", "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/361.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/27"
            ),
            url = "https://rickandmortyapi.com/api/character/361",
            created = "2018-01-10T18:20:41.703Z"
        )
    )

    fun getItems(tab: ItemsTab): List<Item> =
        if (tab.status == null) items else items.filter { it.status == tab.status }

    fun getItem(id: Int): Item? = items.firstOrNull { it.id == id }
}