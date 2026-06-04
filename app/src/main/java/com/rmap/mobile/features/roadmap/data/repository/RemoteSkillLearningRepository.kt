package com.rmap.mobile.features.roadmap.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.mapper.toDomain
import com.rmap.mobile.features.roadmap.data.remote.api.SkillApi
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RemoteSkillLearningRepository(
    private val skillApi: SkillApi,
    private val sessionManager: SessionManager
) : SkillLearningRepository {
    override suspend fun getSkillLearningContent(skillId: String): Result<SkillLearningContent> {
        val normalizedSkillId = skillId.trim()
        if (normalizedSkillId.isBlank()) {
            return Result.failure(invalidSkillId())
        }

        val (skillResult, resourcesResult) = coroutineScope {
            val skillDeferred = async { getSkillResult(normalizedSkillId) }
            val resourcesDeferred = async { getSkillResourcesResult(normalizedSkillId) }
            skillDeferred.await() to resourcesDeferred.await()
        }

        return when {
            skillResult is NetworkResult.Error -> Result.failure(skillResult.toAppException())
            skillResult is NetworkResult.Success && resourcesResult is NetworkResult.Success -> {
                runCatching {
                    SkillLearningContent(
                        skill = skillResult.data.toDomain(),
                        resources = resourcesResult.data.toDomain()
                    )
                }.recoverCatching { error ->
                    throw invalidSkillResponse(skillResult.code, error)
                }
            }

            skillResult is NetworkResult.Success &&
                resourcesResult is NetworkResult.Error &&
                resourcesResult.type == NetworkErrorType.NotFound -> {
                runCatching {
                    SkillLearningContent(
                        skill = skillResult.data.toDomain(),
                        resources = emptyList()
                    )
                }.recoverCatching { error ->
                    throw invalidSkillResponse(skillResult.code, error)
                }
            }

            resourcesResult is NetworkResult.Error -> Result.failure(resourcesResult.toAppException())

            else -> Result.failure(invalidSkillResponse())
        }
    }

    private suspend fun getSkillResult(skillId: String) = SafeApiCall.execute(
        onUnauthorized = sessionManager::handleUnauthorized
    ) {
        skillApi.getSkill(skillId)
    }

    private suspend fun getSkillResourcesResult(skillId: String) = SafeApiCall.execute(
        onUnauthorized = sessionManager::handleUnauthorized
    ) {
        skillApi.getSkillResources(skillId)
    }

    private fun invalidSkillId(): AppException {
        return AppException(
            message = "Skill id is required.",
            type = NetworkErrorType.Unknown
        )
    }

    private fun invalidSkillResponse(
        statusCode: Int? = null,
        cause: Throwable? = null
    ): AppException {
        return AppException(
            message = "Unable to read skill response. Please try again.",
            code = statusCode,
            type = NetworkErrorType.Serialization,
            cause = cause
        )
    }
}
