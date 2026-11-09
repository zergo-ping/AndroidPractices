package com.example.androidpractise.domain.repository

import com.example.androidpractise.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observe(): Flow<Profile>
    suspend fun save(profile: Profile)
    suspend fun importAvatar(uri: String): String
    suspend fun downloadResume(url: String): String
}
