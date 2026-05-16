package com.rmap.mobile.features.profile.data

import com.rmap.mobile.features.profile.domain.model.UserProfile
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository

class FakeProfileRepository : ProfileRepository {
    override suspend fun getProfile(): Result<UserProfile> {
        return Result.success(
            UserProfile(
                userName = "Thinh",
                name = "Thinh Duy",
                role = "Aspiring Frontend Developer",
                avatarUrl = "",
                xp = 450,
                streakDays = 5,
                certificates = 2
            )
        )
    }
}
