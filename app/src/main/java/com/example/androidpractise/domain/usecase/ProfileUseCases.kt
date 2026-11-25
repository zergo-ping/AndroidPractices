package com.example.androidpractise.domain.usecase

import com.example.androidpractise.domain.model.Profile
import com.example.androidpractise.domain.repository.ProfileRepository
import java.net.URI

class ObserveProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke() = repository.observe()
}

class SaveProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) {
        require(profile.resumeUrl.isBlank() || isValidResumeUrl(profile.resumeUrl)) {
            "Укажите корректную ссылку HTTPS на файл резюме"
        }
        repository.save(profile.copy(fullName = profile.fullName.trim(), resumeUrl = profile.resumeUrl.trim()))
    }
}

class ImportAvatarUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(uri: String) = repository.importAvatar(uri)
}

class DownloadResumeUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(url: String): String {
        require(isValidResumeUrl(url)) { "Укажите корректную ссылку HTTPS на файл резюме" }
        return repository.downloadResume(url.trim())
    }
}

fun isValidResumeUrl(value: String): Boolean = try {
    val uri = URI(value.trim())
    uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() && uri.userInfo == null
} catch (_: IllegalArgumentException) {
    false
} catch (_: java.net.URISyntaxException) {
    false
}
