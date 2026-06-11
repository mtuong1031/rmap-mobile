package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.UserActivitySummary
import com.rmap.mobile.features.profile.domain.model.UserProfileIdentity

interface ProfileRepository {
    suspend fun getActivity(): Result<UserActivitySummary>

    suspend fun updateProfile(
        fullName: String,
        avatarUrl: String
    ): Result<UserProfileIdentity>

    suspend fun getIntegrations(): Result<List<com.rmap.mobile.features.profile.domain.model.UserIntegration>>

    suspend fun disconnectIntegration(provider: String): Result<Unit>
}
