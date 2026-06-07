package com.rmap.mobile.features.profile.data.repository

import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.profile.data.mapper.toDomain
import com.rmap.mobile.features.profile.data.remote.ProfileApi
import com.rmap.mobile.features.profile.domain.model.UserActivitySummary
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val sessionManager: SessionManager
) : ProfileRepository {
    override suspend fun getActivity(): Result<UserActivitySummary> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            profileApi.getActivity()
        }.toDomainResult { activity ->
            activity.toDomain()
        }
    }
}
