package com.example.androidpractise.profile.ui

import android.Manifest
import android.app.TimePickerDialog
import android.provider.Settings
import androidx.compose.ui.res.painterResource
import com.example.androidpractise.R
import com.example.androidpractise.domain.model.isValidClassTime
import com.example.androidpractise.profile.reminder.ClassReminder
import java.util.Calendar
import java.util.Locale
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.example.androidpractise.profile.viewmodel.ProfileEffect
import com.example.androidpractise.profile.viewmodel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(viewModel: ProfileViewModel, editing: Boolean, onBack: () -> Unit, onEdit: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val time by viewModel.classTime.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val resume by viewModel.resume.collectAsStateWithLifecycle()
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var chooseSource by rememberSaveable { mutableStateOf(false) }
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    var permissionReady by rememberSaveable { mutableStateOf(!editing) }

    val reminderSettings = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (ClassReminder.canSchedule(context)) viewModel.save()
        else viewModel.reportError("Для напоминания в выбранную минуту разрешите точные будильники")
    }
    fun scheduleAfterPermission() {
        if (!ClassReminder.prepareChannel(context)) {
            viewModel.reportError("Включите уведомления «Начало пары» в настройках приложения")
        } else if (Build.VERSION.SDK_INT >= 31 && !ClassReminder.canSchedule(context)) {
            try {
                reminderSettings.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    "package:${context.packageName}".toUri()))
            } catch (_: ActivityNotFoundException) {
                viewModel.reportError("Разрешите точные будильники в настройках приложения")
            }
        } else viewModel.save()
    }
    val notifications = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) scheduleAfterPermission()
        else viewModel.reportError("Для напоминания разрешите уведомления в настройках приложения")
    }

    val storagePermissions = remember {
        when {
            Build.VERSION.SDK_INT >= 34 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            Build.VERSION.SDK_INT >= 33 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    fun hasPhotoAccess() = storagePermissions.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    val photoPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it } || hasPhotoAccess()) permissionReady = true else onBack()
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.selectAvatar(it.toString()) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = viewModel.cameraUri
        if (success && uri != null) viewModel.selectAvatar(uri)
        viewModel.cameraUri = null
    }
    fun takePhoto() {
        try {
            val directory = File(context.cacheDir, "camera").apply { mkdirs() }
            val file = File.createTempFile("avatar-", ".jpg", directory)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
            viewModel.cameraUri = uri.toString()
            camera.launch(uri)
        } catch (_: ActivityNotFoundException) {
            viewModel.reportError("Приложение камеры не найдено")
        } catch (_: Exception) {
            viewModel.reportError("Не удалось запустить камеру")
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) takePhoto() else viewModel.reportError("Для съёмки разрешите доступ к камере в настройках приложения")
    }
    LaunchedEffect(editing) {
        if (editing && !permissionRequested) {
            permissionRequested = true
            if (hasPhotoAccess()) permissionReady = true else photoPermission.launch(storagePermissions)
        }
    }
    LaunchedEffect(viewModel, lifecycleOwner) {
        viewModel.effects.flowWithLifecycle(lifecycleOwner.lifecycle).collect { effect ->
            when (effect) {
                ProfileEffect.Saved -> onBack()
                is ProfileEffect.OpenDocument -> {
                    val extension = effect.uri.toUri().lastPathSegment?.substringAfterLast('.').orEmpty()
                    val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(effect.uri.toUri(), mime)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            clipData = android.content.ClipData.newRawUri("Резюме", effect.uri.toUri())
                        })
                    } catch (_: ActivityNotFoundException) {
                        viewModel.reportError("Файл скачан. Установите приложение для просмотра этого формата")
                    } catch (_: Exception) {
                        viewModel.reportError("Не удалось открыть скачанный файл")
                    }
                }
            }
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text(if (editing) "Редактирование профиля" else "Профиль") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
            actions = {
                if (!editing) IconButton(onClick = onEdit, enabled = !state.loading) {
                    Icon(Icons.Default.Edit, "Редактировать профиль")
                }
            })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            .imePadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (state.loading || !permissionReady) {
                CircularProgressIndicator()
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                return@Column
            }
            val photo = if (editing) avatar else state.profile.avatarUri
            Box(Modifier.size(144.dp).clip(CircleShape)
                .clickable(enabled = editing && !state.busy) { chooseSource = true }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, "Аватарка", Modifier.size(100.dp))
                if (photo.isNotBlank()) AsyncImage(model = photo, contentDescription = "Фото профиля",
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            if (editing) {
                Text("Нажмите на фото, чтобы выбрать аватарку")
                OutlinedTextField(value = name, onValueChange = viewModel::changeName,
                    label = { Text("ФИО") }, enabled = !state.busy, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = resume, onValueChange = viewModel::changeResume,
                    label = { Text("Ссылка HTTPS на файл резюме") }, enabled = !state.busy,
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri))
                val validTime = isValidClassTime(time)
                OutlinedTextField(value = time, onValueChange = viewModel::changeTime,
                    label = { Text("Время любимой пары") }, placeholder = { Text("HH:mm") },
                    singleLine = true, enabled = !state.busy, isError = !validTime,
                    supportingText = { Text(if (validTime) "Напоминание на сегодня" else "Введите время в формате HH:mm") },
                    trailingIcon = {
                        IconButton(enabled = !state.busy, onClick = {
                            val now = Calendar.getInstance()
                            val hour = if (validTime) time.take(2).toInt() else now.get(Calendar.HOUR_OF_DAY)
                            val minute = if (validTime) time.takeLast(2).toInt() else now.get(Calendar.MINUTE)
                            TimePickerDialog(context, { _, h, m ->
                                viewModel.changeTime(String.format(Locale.ROOT, "%02d:%02d", h, m))
                            }, hour, minute, true).show()
                        }) { Icon(painterResource(R.drawable.ic_class_clock), "Выбрать время") }
                    }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,
                            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else scheduleAfterPermission()
                }, enabled = validTime && !state.busy) { Text("Готово") }
            } else {
                Text(state.profile.fullName.ifBlank { "Имя не указано" }, style = MaterialTheme.typography.headlineSmall)
                if (state.profile.classTime.isNotEmpty()) Text("Время любимой пары: ${state.profile.classTime}")
                Text(state.profile.resumeUrl.ifBlank { "Резюме не добавлено" })
                Button(onClick = viewModel::openResume, enabled = !state.busy && state.profile.resumeUrl.isNotBlank()) {
                    Text("Резюме")
                }
            }
            if (state.busy) CircularProgressIndicator()
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
    if (chooseSource) AlertDialog(onDismissRequest = { chooseSource = false },
        title = { Text("Новая аватарка") }, text = { Text("Откуда взять фотографию?") },
        confirmButton = { TextButton(onClick = {
            chooseSource = false
            try { gallery.launch(arrayOf("image/*")) }
            catch (_: ActivityNotFoundException) { viewModel.reportError("Приложение галереи не найдено") }
        }) { Text("Галерея") } },
        dismissButton = { TextButton(onClick = {
            chooseSource = false
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) takePhoto()
            else cameraPermission.launch(Manifest.permission.CAMERA)
        }) { Text("Камера") } })
}
