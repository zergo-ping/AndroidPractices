package com.example.androidpractise.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.androidpractise.domain.model.ItemFilters
import com.example.androidpractise.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FiltersRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : FiltersRepository {

    override fun observeFilters(): Flow<ItemFilters> = dataStore.data.map { preferences ->
        ItemFilters(
            status = preferences[STATUS_KEY]?.takeIf { it.isNotBlank() },
            species = preferences[SPECIES_KEY].orEmpty(),
            gender = preferences[GENDER_KEY]?.takeIf { it.isNotBlank() }
        )
    }

    override suspend fun saveFilters(filters: ItemFilters) {
        dataStore.edit { preferences ->
            filters.status?.let { preferences[STATUS_KEY] = it }
                ?: preferences.remove(STATUS_KEY)

            if (filters.species.isBlank()) {
                preferences.remove(SPECIES_KEY)
            } else {
                preferences[SPECIES_KEY] = filters.species.trim()
            }

            filters.gender?.let { preferences[GENDER_KEY] = it }
                ?: preferences.remove(GENDER_KEY)
        }
    }

    private companion object {
        val STATUS_KEY = stringPreferencesKey("filter_status")
        val SPECIES_KEY = stringPreferencesKey("filter_species")
        val GENDER_KEY = stringPreferencesKey("filter_gender")
    }
}
