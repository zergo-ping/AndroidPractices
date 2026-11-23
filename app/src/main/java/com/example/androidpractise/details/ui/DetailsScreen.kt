package com.example.androidpractise.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.mvi.DetailsState
import com.example.androidpractise.details.viewmodel.DetailsViewModel
import com.example.androidpractise.domain.model.Item

@Composable
fun DetailsRoute(
    viewModel: DetailsViewModel,
    itemId: Int,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.handleIntent(DetailsIntent.LoadItem(itemId))
    }

    DetailsScreen(
        state = state,
        onBack = onBack,
        onRetry = { viewModel.handleIntent(DetailsIntent.LoadItem(itemId)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = state.item?.name ?: "") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val item = state.item
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                item == null -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
<<<<<<< HEAD
                        text = state.error ?: "Не удалось загрузить данные",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
=======
<<<<<<< HEAD
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.constrainAs(name) {
                            top.linkTo(image.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.status,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(status) {
                            top.linkTo(name.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.species,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(species) {
                            top.linkTo(status.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.type,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(type) {
                            top.linkTo(species.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.gender,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(gender) {
                            top.linkTo(type.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.origin,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(origin) {
                            top.linkTo(gender.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(location) {
                            top.linkTo(origin.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = item.created,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(created) {
                            top.linkTo(location.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
=======
                        text = state.error ?: "Не удалось загрузить данные",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
>>>>>>> 932a0dc (Practise2)
>>>>>>> 564097e (conflict resolution)
                    )
                    Button(onClick = onRetry) {
                        Text(text = "Повторить")
                    }
                }

                else -> DetailsContent(item = item)
            }
        }
    }
}

@Composable
private fun DetailsContent(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Text(
            text = item.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        DetailRow(label = "Статус", value = item.status)
        DetailRow(label = "Вид", value = item.species)
        DetailRow(label = "Тип", value = item.type.ifBlank { "—" })
        DetailRow(label = "Пол", value = item.gender)
        DetailRow(label = "Происхождение", value = item.origin.name)
        DetailRow(label = "Локация", value = item.location.name)
        DetailRow(label = "Эпизодов", value = item.episode.size.toString())
        DetailRow(label = "Создан", value = item.created)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}