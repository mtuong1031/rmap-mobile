package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.UserActivitySummary

interface ProfileRepository {
    suspend fun getActivity(): Result<UserActivitySummary>
}
