package com.example.androidpractise.filters.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidpractise.filters.mvi.FiltersEffect
import com.example.androidpractise.filters.mvi.FiltersIntent
import com.example.androidpractise.filters.mvi.FiltersState
import com.example.androidpractise.filters.viewmodel.FiltersViewModel

@Composable
fun FiltersRoute(
    viewModel: FiltersViewModel,
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                FiltersEffect.Close -> onClose()
            }
        }
    }

    FiltersScreen(
        state = state,
        onIntent = viewModel::handleIntent,
        onBack = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    state: FiltersState,
    onIntent: (FiltersIntent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Фильтры") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OptionGroup(title = "Статус") {
                StatusOptions.forEach { option ->
                    FilterChip(
                        selected = state.draft.status == option.value,
                        onClick = {
                            onIntent(FiltersIntent.StatusChanged(option.value))
                        },
                        label = { Text(text = option.title) }
                    )
                }
            }

            OptionGroup(title = "Пол") {
                GenderOptions.forEach { option ->
                    FilterChip(
                        selected = state.draft.gender == option.value,
                        onClick = {
                            onIntent(FiltersIntent.GenderChanged(option.value))
                        },
                        label = { Text(text = option.title) }
                    )
                }
            }

            OptionGroup(title = "Вид") {
                OutlinedTextField(
                    value = state.draft.species,
                    onValueChange = { onIntent(FiltersIntent.SpeciesChanged(it)) },
                    singleLine = true,
                    placeholder = { Text(text = "Например, Human") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onIntent(FiltersIntent.Apply) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Text(text = "Готово", modifier = Modifier.padding(start = 8.dp))
                }

                OutlinedButton(
                    onClick = { onIntent(FiltersIntent.Reset) },
                    enabled = !state.isDefault,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Text(text = "Сбросить", modifier = Modifier.padding(start = 8.dp))
                }

                if (!state.isDefault) {
                    Text(
                        text = "Фильтры применены",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }
}

private data class FilterOption(val title: String, val value: String?)

private val StatusOptions = listOf(
    FilterOption("Любой", null),
    FilterOption("Живой", "Alive"),
    FilterOption("Мёртвый", "Dead"),
    FilterOption("Неизвестно", "unknown")
)

private val GenderOptions = listOf(
    FilterOption("Любой", null),
    FilterOption("Мужской", "Male"),
    FilterOption("Женский", "Female"),
    FilterOption("Неизвестно", "unknown")
)
