package com.example.androidpractise.details.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.androidpractise.details.mvi.DetailsIntent
import com.example.androidpractise.details.mvi.DetailsState
import com.example.androidpractise.details.viewmodel.DetailsViewModel

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
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onBack: () -> Unit
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
                            contentDescription = null
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
            if (state.isLoading || item == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (image, name, status, species, type, gender, origin, location, created) =
                        createRefs()

                    AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .constrainAs(image) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .size(200.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.constrainAs(name) {
                            top.linkTo(image.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Статус: ${item.status}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(status) {
                            top.linkTo(name.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Вид: ${item.species}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(species) {
                            top.linkTo(status.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Тип: ${item.type}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(type) {
                            top.linkTo(species.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Пол: ${item.gender}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(gender) {
                            top.linkTo(type.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Происхождение: ${item.origin.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(origin) {
                            top.linkTo(gender.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Локация: ${item.location.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(location) {
                            top.linkTo(origin.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Создан: ${item.created}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.constrainAs(created) {
                            top.linkTo(location.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                }
            }
        }
    }
}
