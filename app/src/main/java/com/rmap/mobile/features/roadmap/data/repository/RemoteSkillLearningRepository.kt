package com.rmap.mobile.features.roadmap.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.database.entity.SyncDataType
import com.rmap.mobile.core.database.sync.SyncManager
import com.rmap.mobile.core.database.sync.SyncVersionDto
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.local.dao.SkillDao
import com.rmap.mobile.features.roadmap.data.local.mapper.toEntity
import com.rmap.mobile.features.roadmap.data.local.mapper.toSkillDetail
import com.rmap.mobile.features.roadmap.data.local.mapper.toSkillResource
import com.rmap.mobile.features.roadmap.data.mapper.toDomain
import com.rmap.mobile.features.roadmap.data.remote.api.SkillApi
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RemoteSkillLearningRepository(
    private val skillApi: SkillApi,
    private val sessionManager: SessionManager,
    private val skillDao: SkillDao? = null,
    private val syncManager: SyncManager? = null
) : SkillLearningRepository {
    override suspend fun getSkillLearningContent(skillId: String): Result<SkillLearningContent> {
        val normalizedSkillId = skillId.trim()
        if (normalizedSkillId.isBlank()) {
            return Result.failure(invalidSkillId())
        }

        val serverVersions = syncManager?.getServerVersions()
        cachedSkillLearningContentIfFresh(
            skillId = normalizedSkillId,
            serverVersions = serverVersions
        )?.let { cachedContent ->
            return Result.success(cachedContent)
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
                    cacheSkillLearningContent(
                        skillId = normalizedSkillId,
                        skillResult = skillResult,
                        resourcesResult = resourcesResult,
                        serverVersions = serverVersions
                    )
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
                    cacheSkillLearningContent(
                        skillId = normalizedSkillId,
                        skillResult = skillResult,
                        resourcesResult = null,
                        serverVersions = serverVersions
                    )
                    SkillLearningContent(
                        skill = skillResult.data.toDomain(),
                        resources = emptyList()
                    )
                }.recoverCatching { error ->
                    throw invalidSkillResponse(skillResult.code, error)
                }
            }

            resourcesResult is NetworkResult.Error -> {
                cachedSkillLearningContent(normalizedSkillId)
                    ?.let { cachedContent -> Result.success(cachedContent) }
                    ?: Result.failure(resourcesResult.toAppException())
            }

            else -> cachedSkillLearningContent(normalizedSkillId)
                ?.let { cachedContent -> Result.success(cachedContent) }
                ?: Result.failure(invalidSkillResponse())
        }
    }

    private suspend fun cachedSkillLearningContentIfFresh(
        skillId: String,
        serverVersions: SyncVersionDto?
    ): SkillLearningContent? {
        val dao = skillDao ?: return null
        val skill = dao.getSkill(skillId) ?: return null
        val skillStale = syncManager?.isStale(SyncDataType.skill(skillId), serverVersions) ?: true
        val resourcesStale = syncManager?.isStale(SyncDataType.resources(skillId), serverVersions) ?: true
        if (skillStale || resourcesStale) return null

        return SkillLearningContent(
            skill = skill.toSkillDetail(),
            resources = dao.getResources(skillId).map { resource -> resource.toSkillResource() }
        )
    }

    private suspend fun cachedSkillLearningContent(skillId: String): SkillLearningContent? {
        val dao = skillDao ?: return null
        val skill = dao.getSkill(skillId) ?: return null
        return SkillLearningContent(
            skill = skill.toSkillDetail(),
            resources = dao.getResources(skillId).map { resource -> resource.toSkillResource() }
        )
    }

    private suspend fun cacheSkillLearningContent(
        skillId: String,
        skillResult: NetworkResult.Success<com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto>,
        resourcesResult: NetworkResult.Success<com.rmap.mobile.features.roadmap.data.remote.model.SkillResourcesResponseDto>?,
        serverVersions: SyncVersionDto?
    ) {
        val dao = skillDao ?: return
        val skill = skillResult.data.toEntity()
        val resources = resourcesResult?.data?.data.orEmpty()
            .map { resource -> resource.toEntity(defaultSkillId = skillId) }

        dao.replaceSkillWithResources(skill, resources)
        syncManager?.markSynced(SyncDataType.skill(skillId), serverVersions)
        syncManager?.markSynced(SyncDataType.resources(skillId), serverVersions)
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
