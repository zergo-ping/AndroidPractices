package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.Profile
import com.example.androidpractise.domain.repository.ProfileRepository
import com.example.androidpractise.domain.model.isValidClassTime
import java.net.URI

internal class ObserveProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke() = repository.observe()
}

internal class SaveProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) {
        require(isValidClassTime(profile.classTime)) { "Введите время в формате HH:mm" }
        require(profile.resumeUrl.isBlank() || isValidResumeUrl(profile.resumeUrl)) {
            "Укажите корректную ссылку HTTPS на файл резюме"
        }
        repository.save(profile.copy(fullName = profile.fullName.trim(), resumeUrl = profile.resumeUrl.trim()))
    }
}

internal class ImportAvatarUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(uri: String) = repository.importAvatar(uri)
}

internal class DownloadResumeUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(url: String): String {
        require(isValidResumeUrl(url)) { "Укажите корректную ссылку HTTPS на файл резюме" }
        return repository.downloadResume(url.trim())
    }
}

internal fun isValidResumeUrl(value: String): Boolean = try {
    val uri = URI(value.trim())
    uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() && uri.userInfo == null
} catch (_: IllegalArgumentException) {
    false
} catch (_: java.net.URISyntaxException) {
    false
}
