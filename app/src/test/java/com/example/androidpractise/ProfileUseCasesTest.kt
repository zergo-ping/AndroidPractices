package com.example.androidpractise

import com.example.androidpractise.domain.model.Profile
import com.example.androidpractise.domain.repository.ProfileRepository
import com.example.androidpractise.domain.usecase.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProfileUseCasesTest {
    private class FakeRepository : ProfileRepository {
        var saved = Profile()
        var downloads = 0
        override fun observe() = flowOf(saved)
        override suspend fun save(profile: Profile) { saved = profile }
        override suspend fun importAvatar(uri: String) = uri
        override suspend fun downloadResume(url: String): String { downloads++; return "content://resume" }
    }
    @Test fun saveTrimsFieldsAndRequiresClassTime() = runBlocking {
        val repository = FakeRepository()
        val save = SaveProfileUseCase(repository)
        save(Profile("  Иван Иванов  ", "file:///avatar.jpg", "  https://example.org/cv.pdf  ", "00:00"))
        assertEquals(Profile("Иван Иванов", "file:///avatar.jpg", "https://example.org/cv.pdf", "00:00"), repository.saved)
        save(Profile(classTime = "23:59"))
        for (time in listOf("", "9:00", "24:00", "12:60", "12:3", " noon ", " 09:00")) {
            try { save(Profile(classTime = time)); fail("Accepted $time") }
            catch (_: IllegalArgumentException) { }
        }
        assertEquals(Profile(classTime = "23:59"), repository.saved)
    }
    @Test fun invalidLinksNeverReachStorageOrNetwork() = runBlocking {
        val repository = FakeRepository()
        for (url in listOf("http://example.org/cv.pdf", "file:///etc/passwd", "javascript:alert(1)", "not a url")) {
            try { SaveProfileUseCase(repository)(Profile(resumeUrl = url, classTime = "12:30")); fail("Accepted $url") }
            catch (_: IllegalArgumentException) { }
            try { DownloadResumeUseCase(repository)(url); fail("Downloaded $url") }
            catch (_: IllegalArgumentException) { }
        }
        assertEquals(Profile(), repository.saved)
        assertEquals(0, repository.downloads)
    }
    @Test fun downloadUsesValidatedLink() = runBlocking {
        val repository = FakeRepository()
        assertEquals("content://resume", DownloadResumeUseCase(repository)("https://example.org/cv.pdf"))
        assertEquals(1, repository.downloads)
    }
}
