package com.example.androidpractise.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.filtersDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "item_filters"
)
