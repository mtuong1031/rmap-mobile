package com.rmap.mobile.features.roadmap.data.repository

import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.mapper.toCategories
import com.rmap.mobile.features.roadmap.data.mapper.toDetail
import com.rmap.mobile.features.roadmap.data.mapper.toDomain
import com.rmap.mobile.features.roadmap.data.mapper.toLearningProgress
import com.rmap.mobile.features.roadmap.data.mapper.toRoadmapSummary
import com.rmap.mobile.features.roadmap.data.mapper.toSummary
import com.rmap.mobile.features.roadmap.data.mapper.toDomain as toTemplateCategoryDomain
import com.rmap.mobile.features.roadmap.data.mapper.toSubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapProgressSummaryDto
import com.rmap.mobile.features.roadmap.data.remote.RoadmapApi
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

class RoadmapRepositoryImpl(
    private val roadmapApi: RoadmapApi,
    private val sessionManager: SessionManager
) : RoadmapRepository {
    private var cachedSummaries: List<RoadmapSummary>? = null
    private var cachedTemplateSummaries: List<RoadmapSummary>? = null
    private var cachedTemplateCategories: List<RoadmapCategory>? = null
    private var cachedProgress: List<RoadmapProgressSummaryDto>? = null

    override suspend fun getLearningProgress(): Result<LearningProgress> {
        return loadProgressSummaries().map { progress ->
            progress.toLearningProgress()
        }
    }

    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> {
        return loadRoadmapSummaries().map { roadmaps ->
            roadmaps.take(TRENDING_LIMIT)
        }
    }

    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
        cachedTemplateCategories?.let { categories -> return Result.success(categories) }

        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listTemplateCategories()
        }

        return networkResult.toDomainResult { response ->
            response.categories.map { category -> category.toTemplateCategoryDomain() }
        }.onSuccess { categories ->
            cachedTemplateCategories = categories
        }
    }

    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> {
        return loadRoadmapSummaries().map { roadmaps ->
            roadmaps.take(RECOMMENDED_LIMIT)
        }
    }

    override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
        val normalizedQuery = query.trim()
        return loadTemplateSummaries().map { roadmaps ->
            if (normalizedQuery.isBlank()) {
                roadmaps
            } else {
                roadmaps.filter { roadmap ->
                    roadmap.title.contains(normalizedQuery, ignoreCase = true)
                }
            }
        }
    }

    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
        val roadmapResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getRoadmap(id)
        }

        val nodesResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listRoadmapNodes(id)
        }

        return when {
            roadmapResult is NetworkResult.Error -> Result.failure(roadmapResult.toAppException())
            nodesResult is NetworkResult.Error -> Result.failure(nodesResult.toAppException())
            roadmapResult is NetworkResult.Success && nodesResult is NetworkResult.Success -> {
                runCatching {
                    roadmapResult.data.toDetail(nodesResult.data.nodes)
                }
            }
            else -> Result.failure(IllegalStateException("Unable to load roadmap detail."))
        }
    }

    override suspend fun getLearningNode(
        roadmapId: String,
        nodeId: String
    ): Result<LearningNodeDetail> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getNodeDetail(
                roadmapId = roadmapId,
                nodeId = nodeId
            )
        }

        return networkResult.toDomainResult { response -> response.toDomain() }
    }

    override suspend fun getNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): Result<NodeQuiz> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getNodeQuiz(
                roadmapId = roadmapId,
                nodeId = nodeId
            )
        }

        return networkResult.toDomainResult { response -> response.toDomain() }
    }

    override suspend fun submitNodeQuiz(
        roadmapId: String,
        nodeId: String,
        answers: List<NodeQuizAnswer>
    ): Result<NodeQuizSubmissionResult> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.submitNodeQuiz(
                roadmapId = roadmapId,
                nodeId = nodeId,
                request = answers.toSubmitQuizRequestDto()
            )
        }

        return networkResult.toDomainResult { response -> response.toDomain() }
    }

    private suspend fun loadRoadmapSummaries(): Result<List<RoadmapSummary>> {
        cachedSummaries?.let { summaries -> return Result.success(summaries) }

        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listRoadmaps()
        }

        return networkResult.toDomainResult { response ->
            val progressByRoadmapId = loadProgressForRoadmaps(response.data.map { it.id })
                .also { progress -> cachedProgress = progress }
                .associateBy { it.roadmapId }

            response.data.map { roadmap ->
                roadmap.toSummary(progressByRoadmapId[roadmap.id])
            }
        }.onSuccess { summaries ->
            cachedSummaries = summaries
        }
    }

    private suspend fun loadTemplateSummaries(): Result<List<RoadmapSummary>> {
        cachedTemplateSummaries?.let { summaries -> return Result.success(summaries) }

        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listTemplates(perPage = TEMPLATE_PAGE_SIZE)
        }

        return networkResult.toDomainResult { response ->
            response.data.map { template -> template.toRoadmapSummary() }
        }.onSuccess { summaries ->
            cachedTemplateSummaries = summaries
        }
    }

    private suspend fun loadProgressSummaries(): Result<List<RoadmapProgressSummaryDto>> {
        cachedProgress?.let { progress -> return Result.success(progress) }

        val roadmapsResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listRoadmaps()
        }

        return roadmapsResult.toDomainResult { response ->
            loadProgressForRoadmaps(response.data.map { it.id })
        }.onSuccess { progress ->
            cachedProgress = progress
        }
    }

    private suspend fun loadProgressForRoadmaps(
        roadmapIds: List<String>
    ): List<RoadmapProgressSummaryDto> {
        return roadmapIds.mapNotNull { roadmapId ->
            when (
                val progressResult = SafeApiCall.execute(
                    onUnauthorized = sessionManager::handleUnauthorized
                ) {
                    roadmapApi.getRoadmapProgress(roadmapId)
                }
            ) {
                is NetworkResult.Success -> progressResult.data
                is NetworkResult.Error -> null
            }
        }
    }

    private companion object {
        const val TRENDING_LIMIT = 4
        const val RECOMMENDED_LIMIT = 4
        const val TEMPLATE_PAGE_SIZE = 100
    }
}
