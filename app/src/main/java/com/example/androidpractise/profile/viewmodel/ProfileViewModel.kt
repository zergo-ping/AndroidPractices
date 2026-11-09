package com.example.androidpractise.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidpractise.di.AndroidPractiseApplication
import com.example.androidpractise.domain.model.Profile
import com.example.androidpractise.domain.usecase.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val profile: Profile = Profile(), val loading: Boolean = true,
    val busy: Boolean = false, val error: String? = null
)
sealed interface ProfileEffect {
    data object Saved : ProfileEffect
    data class OpenDocument(val uri: String) : ProfileEffect
}
class ProfileViewModel(
    private val savedState: SavedStateHandle,
    observe: ObserveProfileUseCase,
    private val saveProfile: SaveProfileUseCase,
    private val importAvatar: ImportAvatarUseCase,
    private val downloadResume: DownloadResumeUseCase
) : ViewModel() {
    private val mutableState = MutableStateFlow(ProfileState())
    val state = mutableState.asStateFlow()
    private val channel = Channel<ProfileEffect>(Channel.BUFFERED)
    val effects = channel.receiveAsFlow()
    val name = savedState.getStateFlow("name", "")
    val resume = savedState.getStateFlow("resume", "")
    val avatar = savedState.getStateFlow("avatar", "")
    var cameraUri: String?
        get() = savedState["camera_uri"]
        set(value) { savedState["camera_uri"] = value }
    init {
        viewModelScope.launch {
            try {
                observe().collect { profile ->
                    if (savedState.get<Boolean>("initialized") != true) {
                        savedState["name"] = profile.fullName
                        savedState["resume"] = profile.resumeUrl
                        savedState["avatar"] = profile.avatarUri
                        savedState["initialized"] = true
                    }
                    mutableState.update { it.copy(profile = profile, loading = false) }
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                mutableState.update { it.copy(loading = false, error = "Не удалось прочитать профиль") }
            }
        }
    }
    fun changeName(value: String) { savedState["name"] = value }
    fun changeResume(value: String) { savedState["resume"] = value }
    fun reportError(message: String) { mutableState.update { it.copy(error = message) } }
    fun selectAvatar(uri: String) = work { savedState["avatar"] = importAvatar(uri) }
    fun save() = work {
        saveProfile(Profile(name.value, avatar.value, resume.value))
        channel.send(ProfileEffect.Saved)
    }
    fun openResume() = work {
        channel.send(ProfileEffect.OpenDocument(downloadResume(state.value.profile.resumeUrl)))
    }
    private fun work(block: suspend () -> Unit) {
        if (state.value.busy || state.value.loading) return
        mutableState.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            try { block() }
            catch (error: Exception) {
                if (error is CancellationException) throw error
                reportError(error.message ?: "Не удалось выполнить действие")
            } finally { mutableState.update { it.copy(busy = false) } }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidPractiseApplication
                val c = app.container
                ProfileViewModel(createSavedStateHandle(), c.observeProfileUseCase,
                    c.saveProfileUseCase, c.importAvatarUseCase, c.downloadResumeUseCase)
            }
        }
    }
}
