package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
}
