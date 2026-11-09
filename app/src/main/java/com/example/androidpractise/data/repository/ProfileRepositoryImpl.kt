package com.example.androidpractise.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidpractise.domain.model.Profile
import com.example.androidpractise.domain.repository.ProfileRepository
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

private val Context.profileDataStore by preferencesDataStore(name = "profile")

class ProfileRepositoryImpl(private val context: Context) : ProfileRepository {
    private val nameKey = stringPreferencesKey("full_name")
    private val avatarKey = stringPreferencesKey("avatar_uri")
    private val resumeKey = stringPreferencesKey("resume_url")
    private val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS).callTimeout(90, TimeUnit.SECONDS)
        .followSslRedirects(false).build()

    override fun observe() = context.profileDataStore.data.map {
        Profile(it[nameKey].orEmpty(), it[avatarKey].orEmpty(), it[resumeKey].orEmpty())
    }
    override suspend fun save(profile: Profile) {
        context.profileDataStore.edit {
            it[nameKey] = profile.fullName
            it[avatarKey] = profile.avatarUri
            it[resumeKey] = profile.resumeUrl
        }
    }
    override suspend fun importAvatar(uri: String): String = withContext(Dispatchers.IO) {
        val directory = File(context.filesDir, "avatars").apply { mkdirs() }
        val file = File(directory, "${UUID.randomUUID()}.jpg")
        try {
            val input = context.contentResolver.openInputStream(uri.toUri())
                ?: throw IOException("Не удалось открыть фотографию")
            input.use { source -> file.outputStream().use { output ->
                copyLimited(source, output, 25L * 1024 * 1024)
            } }
            Uri.fromFile(file).toString()
        } catch (error: Exception) {
            file.delete()
            throw error
        }
    }
    override suspend fun downloadResume(url: String): String = withContext(Dispatchers.IO) {
        client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Ошибка скачивания: ${response.code}")
            val body = response.body ?: throw IOException("Пустой ответ сервера")
            val mime = body.contentType()?.toString()?.substringBefore(';').orEmpty()
            if (mime == "text/html") throw IOException("Укажите прямую ссылку на файл, а не страницу сайта")
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
                ?: response.request.url.pathSegments.lastOrNull()?.substringAfterLast('.', "")
                    ?.takeIf { it.matches(Regex("[a-zA-Z0-9]{1,8}")) } ?: "pdf"
            val directory = File(context.filesDir, "resumes").apply { mkdirs() }
            val file = File(directory, "resume-${UUID.randomUUID()}.$extension")
            try {
                body.byteStream().use { input -> file.outputStream().use { output ->
                    copyLimited(input, output, 50L * 1024 * 1024)
                } }
                FileProvider.getUriForFile(context, "${context.packageName}.files", file).toString()
            } catch (error: Exception) {
                file.delete()
                throw error
            }
        }
    }
    private fun copyLimited(input: java.io.InputStream, output: java.io.OutputStream, limit: Long) {
        val buffer = ByteArray(8192)
        var total = 0L
        while (true) {
            val count = input.read(buffer)
            if (count == -1) break
            total += count
            if (total > limit) throw IOException("Файл слишком большой")
            output.write(buffer, 0, count)
        }
        if (total == 0L) throw IOException("Пустой файл")
    }
}
