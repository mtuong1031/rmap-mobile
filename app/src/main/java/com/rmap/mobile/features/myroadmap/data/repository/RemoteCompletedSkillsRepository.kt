package com.rmap.mobile.features.myroadmap.data.repository

import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.myroadmap.data.mapper.toDomain
import com.rmap.mobile.features.myroadmap.data.remote.CompletedSkillsApi
import com.rmap.mobile.features.myroadmap.domain.model.CompletedSkillPage
import com.rmap.mobile.features.myroadmap.domain.repository.CompletedSkillsRepository

class RemoteCompletedSkillsRepository(
    private val api: CompletedSkillsApi,
    private val sessionManager: SessionManager
) : CompletedSkillsRepository {
    override suspend fun getCompletedSkills(
        category: String,
        page: Int,
        perPage: Int
    ): Result<CompletedSkillPage> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            api.getCompletedSkills(
                category = category,
                page = page.coerceAtLeast(1),
                perPage = perPage.coerceIn(1, MAX_PAGE_SIZE)
            )
        }.toDomainResult { response -> response.toDomain() }
    }

    private companion object {
        const val MAX_PAGE_SIZE = 100
    }
}
