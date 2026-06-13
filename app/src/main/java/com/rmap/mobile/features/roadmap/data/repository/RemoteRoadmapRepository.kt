package com.rmap.mobile.features.roadmap.data.repository

import com.rmap.mobile.core.network.AppException
import com.google.gson.Gson
import com.rmap.mobile.core.database.entity.SyncDataType
import com.rmap.mobile.core.database.sync.SyncManager
import com.rmap.mobile.core.database.sync.SyncVersionDto
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.local.dao.RoadmapDetailCacheDao
import com.rmap.mobile.features.roadmap.data.local.dao.TemplateCategoryDao
import com.rmap.mobile.features.roadmap.data.local.dao.TemplateRoadmapDao
import com.rmap.mobile.features.roadmap.data.local.mapper.toEntity
import com.rmap.mobile.features.roadmap.data.local.mapper.toRoadmapDetail
import com.rmap.mobile.features.roadmap.data.local.mapper.toRoadmapDetailCacheEntity
import com.rmap.mobile.features.roadmap.data.local.mapper.toRoadmapCategory
import com.rmap.mobile.features.roadmap.data.local.mapper.toRoadmapSummary
import com.rmap.mobile.features.roadmap.data.mapper.toDomain
import com.rmap.mobile.features.roadmap.data.mapper.toLearningNodeDetail
import com.rmap.mobile.features.roadmap.data.mapper.toLearningProgress
import com.rmap.mobile.features.roadmap.data.mapper.toMilestoneDetail
import com.rmap.mobile.features.roadmap.data.mapper.toNodeStatusRequestValue
import com.rmap.mobile.features.roadmap.data.mapper.toRoadmapWithNodes
import com.rmap.mobile.features.roadmap.data.mapper.toSkillLearningContent
import com.rmap.mobile.features.roadmap.data.mapper.toSummary
import com.rmap.mobile.features.roadmap.data.mapper.toSubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.remote.api.RoadmapApi
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapsResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitMilestoneSubmissionRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressResponseDto
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteRoadmapRepository(
    private val roadmapApi: RoadmapApi,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository? = null,
    private val templateCategoryDao: TemplateCategoryDao? = null,
    private val templateRoadmapDao: TemplateRoadmapDao? = null,
    private val syncManager: SyncManager? = null,
    private val roadmapDetailCacheDao: RoadmapDetailCacheDao? = null,
    private val gson: Gson = Gson()
) : RoadmapRepository {
    override suspend fun getLearningProgress(): Result<LearningProgress> {
        return when (val roadmapResult = getRoadmapsResult(page = FIRST_PAGE, perPage = FIRST_ITEM_PAGE_SIZE)) {
            is NetworkResult.Success -> {
                val roadmapId = roadmapResult.data.data.orEmpty()
                    .firstOrNull()
                    ?.id
                    ?.takeIf { it.isNotBlank() }
                    ?: return Result.success(emptyLearningProgress())
                getRoadmapProgressResult(roadmapId).toDomainResult { progress ->
                    progress.toLearningProgress()
                }
            }

            is NetworkResult.Error -> {
                if (roadmapResult.type == NetworkErrorType.NotFound) {
                    Result.success(emptyLearningProgress())
                } else {
                    Result.failure(roadmapResult.toAppException())
                }
            }
        }
    }

    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> {
        val serverVersions = syncManager?.getServerVersions()
        cachedTemplateRoadmapsIfFresh(serverVersions)?.let { cachedRoadmaps ->
            return Result.success(cachedRoadmaps.take(RECOMMENDED_ROADMAP_LIMIT))
        }
        return loadTemplates(hydrateNodes = true)
    }

    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
        val serverVersions = syncManager?.getServerVersions()
        cachedCategoriesIfFresh(serverVersions)?.let { cachedCategories ->
            return Result.success(cachedCategories)
        }

        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.listTemplateCategories()
        }.toDomainResult { response ->
            templateCategoryDao?.replaceAll(response.categories.map { category -> category.toEntity() })
            syncManager?.markSynced(SyncDataType.TEMPLATE_CATEGORIES, serverVersions)
            response.categories.map { it.toDomain() }
        }
    }

    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> {
        val serverVersions = syncManager?.getServerVersions()
        cachedTemplateRoadmapsIfFresh(serverVersions)?.let { cachedRoadmaps ->
            return Result.success(cachedRoadmaps.take(RECOMMENDED_ROADMAP_LIMIT))
        }
        return loadTemplates(hydrateNodes = true).map { roadmaps -> roadmaps.take(RECOMMENDED_ROADMAP_LIMIT) }
    }

    override suspend fun searchRoadmaps(query: String, categoryId: String?, page: Int, perPage: Int): Result<Pair<List<RoadmapSummary>, Int>> {
        val normalizedQuery = query.trim()
        val serverVersions = syncManager?.getServerVersions()
        cachedTemplateSearchResult(
            query = normalizedQuery,
            categoryId = categoryId,
            page = page,
            perPage = perPage,
            serverVersions = serverVersions
        )?.let { cachedResult ->
            return Result.success(cachedResult)
        }

        syncTemplateRoadmapsIfNeeded(serverVersions)
            .onSuccess {
                cachedTemplateSearchResult(
                    query = normalizedQuery,
                    categoryId = categoryId,
                    page = page,
                    perPage = perPage,
                    serverVersions = serverVersions,
                    allowStale = true
                )?.let { cachedResult ->
                    return Result.success(cachedResult)
                }
            }

        val result = getTemplatesResult(categoryId = categoryId, page = page, perPage = perPage)
        return when (result) {
            is NetworkResult.Success -> {
                val totalCount = result.data.meta?.total ?: 0
                loadTemplateSummaries(
                    templates = result.data.data.orEmpty(),
                    hydrateNodes = false,
                    statusCode = result.code
                ).map { roadmaps ->
                    val filteredRoadmaps = if (normalizedQuery.isBlank()) {
                        roadmaps
                    } else {
                        roadmaps.filter { roadmap ->
                            roadmap.title.contains(normalizedQuery, ignoreCase = true) ||
                                roadmap.durationLabel.contains(normalizedQuery, ignoreCase = true) ||
                                roadmap.categoryId.contains(normalizedQuery, ignoreCase = true)
                        }
                    }
                    templateRoadmapDao?.insertAll(filteredRoadmaps.map { roadmap -> roadmap.toEntity() })
                    Pair(filteredRoadmaps, totalCount)
                }
            }
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    private suspend fun cachedCategoriesIfFresh(
        serverVersions: SyncVersionDto?
    ): List<RoadmapCategory>? {
        val categoryDao = templateCategoryDao ?: return null
        val cachedCategories = categoryDao.getAll()
        if (cachedCategories.isEmpty()) return null
        val stale = syncManager?.isStale(SyncDataType.TEMPLATE_CATEGORIES, serverVersions) ?: true
        if (stale) return null
        return cachedCategories.map { category -> category.toRoadmapCategory() }
    }

    private suspend fun cachedTemplateRoadmapsIfFresh(
        serverVersions: SyncVersionDto?
    ): List<RoadmapSummary>? {
        val roadmapDao = templateRoadmapDao ?: return null
        val cachedRoadmaps = roadmapDao.getAll()
        if (cachedRoadmaps.isEmpty()) return null
        val stale = syncManager?.isStale(SyncDataType.TEMPLATE_ROADMAPS, serverVersions) ?: true
        if (stale) return null
        return cachedRoadmaps.map { roadmap -> roadmap.toRoadmapSummary() }
    }

    private suspend fun cachedTemplateSearchResult(
        query: String,
        categoryId: String?,
        page: Int,
        perPage: Int,
        serverVersions: SyncVersionDto?,
        allowStale: Boolean = false
    ): Pair<List<RoadmapSummary>, Int>? {
        val roadmapDao = templateRoadmapDao ?: return null
        val cachedRoadmaps = roadmapDao.getAll()
        if (cachedRoadmaps.isEmpty()) return null
        val stale = syncManager?.isStale(SyncDataType.TEMPLATE_ROADMAPS, serverVersions) ?: true
        if (stale && !allowStale) return null

        val filtered = cachedRoadmaps
            .map { roadmap -> roadmap.toRoadmapSummary() }
            .filter { roadmap ->
                query.isBlank() || roadmap.title.contains(query, ignoreCase = true) ||
                    roadmap.durationLabel.contains(query, ignoreCase = true) ||
                    roadmap.categoryId.contains(query, ignoreCase = true)
            }
            .filter { roadmap ->
                categoryId == null ||
                    roadmap.categoryId == categoryId ||
                    roadmap.categoryId == categoryId.toStableLearningId()
            }
        val safePage = page.coerceAtLeast(FIRST_PAGE)
        val safePerPage = perPage.coerceAtLeast(1)
        val startIndex = (safePage - 1) * safePerPage
        val pageItems = filtered.drop(startIndex).take(safePerPage)
        return pageItems to filtered.size
    }

    private suspend fun syncTemplateRoadmapsIfNeeded(
        serverVersions: SyncVersionDto?
    ): Result<Unit> {
        val roadmapDao = templateRoadmapDao ?: return Result.success(Unit)
        val manager = syncManager ?: return Result.success(Unit)
        val hasCache = roadmapDao.countAll() > 0
        val stale = manager.isStale(SyncDataType.TEMPLATE_ROADMAPS, serverVersions)
        if (hasCache && !stale) return Result.success(Unit)

        return fetchAllTemplateRoadmaps().mapCatching { roadmaps ->
            roadmapDao.replaceAll(roadmaps.map { roadmap -> roadmap.toEntity() })
            manager.markSynced(SyncDataType.TEMPLATE_ROADMAPS, serverVersions)
        }
    }

    private suspend fun fetchAllTemplateRoadmaps(): Result<List<RoadmapSummary>> {
        val roadmaps = mutableListOf<RoadmapSummary>()
        var page = FIRST_PAGE
        var totalPages = FIRST_PAGE

        do {
            when (val result = getTemplatesResult(page = page, perPage = TEMPLATE_SYNC_PAGE_SIZE)) {
                is NetworkResult.Success -> {
                    val pageRoadmaps = result.data.data.orEmpty()
                    loadTemplateSummaries(
                        templates = pageRoadmaps,
                        hydrateNodes = false,
                        statusCode = result.code
                    ).onSuccess { summaries ->
                        roadmaps += summaries
                    }.onFailure { error ->
                        return Result.failure(error)
                    }
                    totalPages = result.data.meta?.totalPages
                        ?: if (pageRoadmaps.size < TEMPLATE_SYNC_PAGE_SIZE) page else page + 1
                }

                is NetworkResult.Error -> return Result.failure(result.toAppException())
            }
            page += 1
        } while (page <= totalPages)

        return Result.success(roadmaps)
    }

    override fun observeRoadmapDetail(roadmapId: String): Flow<Result<RoadmapDetail>> = flow {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank()) {
            emit(Result.failure(invalidRoadmapId()))
            return@flow
        }

        val cached = roadmapDetailCacheDao?.get(normalizedRoadmapId)?.toRoadmapDetail(gson)
        if (cached != null) {
            emit(Result.success(cached))
        }

        val freshResult = getRoadmapDetail(normalizedRoadmapId)
        freshResult.onSuccess { detail ->
            roadmapDetailCacheDao?.upsert(detail.toRoadmapDetailCacheEntity(gson))
            emit(Result.success(detail))
        }

        if (freshResult.isFailure && cached == null) {
            emit(freshResult)
        }
    }

    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
        val roadmapId = id.trim()
        if (roadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }

        val isGuest = authRepository?.authState?.value == AuthState.Unauthenticated
        if (isGuest) {
            return when (val templateResult = getTemplateResult(roadmapId)) {
                is NetworkResult.Success -> loadTemplateRoadmapDetail(
                    templateResult = templateResult,
                    roadmapId = roadmapId,
                )

                is NetworkResult.Error -> Result.failure(templateResult.toAppException())
            }
        }

        val (roadmapResult, nodesResult, progressResult) = coroutineScope {
            val roadmapDeferred = async { getRoadmapResult(roadmapId) }
            val nodesDeferred = async { getRoadmapNodesResult(roadmapId) }
            val progressDeferred = async { getRoadmapProgressResult(roadmapId) }
            Triple(
                roadmapDeferred.await(),
                nodesDeferred.await(),
                progressDeferred.await()
            )
        }

        return when (roadmapResult) {
            is NetworkResult.Success -> loadPersonalRoadmapDetail(
                roadmap = roadmapResult.data,
                roadmapId = roadmapId,
                statusCode = roadmapResult.code,
                nodesResult = nodesResult,
                progressResult = progressResult
            )

            is NetworkResult.Error -> when (roadmapResult.type) {
                NetworkErrorType.NotFound -> {
                    when (val templateResult = getTemplateResult(roadmapId)) {
                        is NetworkResult.Success -> loadTemplateRoadmapDetail(
                            templateResult = templateResult,
                            roadmapId = roadmapId,
                        )

                        is NetworkResult.Error -> Result.failure(templateResult.toAppException())
                    }
                }

                else -> Result.failure(roadmapResult.toAppException())
            }
        }
    }

    override suspend fun getRoadmapNodeLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String
    ): Result<SkillLearningContent> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        val normalizedSkillId = skillId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedNodeId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }
        if (normalizedSkillId.isBlank()) {
            return Result.failure(invalidRoadmapSkillId())
        }

        return getRoadmapNodeDetailResult(
            roadmapId = normalizedRoadmapId,
            nodeId = normalizedNodeId
        ).toDomainResult { response ->
            response.toSkillLearningContent(fallbackSkillId = normalizedSkillId)
        }
    }

    override suspend fun getLearningNode(
        roadmapId: String,
        nodeId: String
    ): Result<LearningNodeDetail> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedNodeId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }

        return getRoadmapNodeDetailResult(
            roadmapId = normalizedRoadmapId,
            nodeId = normalizedNodeId
        ).toDomainResult { response ->
            response.toLearningNodeDetail()
        }
    }

    override suspend fun getMilestoneDetail(
        roadmapId: String,
        milestoneId: String
    ): Result<MilestoneDetail> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedMilestoneId = milestoneId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedMilestoneId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }

        return getRoadmapNodeDetailResult(
            roadmapId = normalizedRoadmapId,
            nodeId = normalizedMilestoneId
        ).toDomainResult { response ->
            response.toMilestoneDetail()
        }
    }

    override suspend fun submitMilestone(
        roadmapId: String,
        milestoneId: String,
        repoUrl: String
    ): Result<MilestoneSubmission> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedMilestoneId = milestoneId.trim()
        val normalizedRepoUrl = repoUrl.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedMilestoneId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }
        if (normalizedRepoUrl.isBlank()) {
            return Result.failure(invalidMilestoneRepoUrl())
        }

        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.submitMilestoneSubmission(
                roadmapId = normalizedRoadmapId,
                nodeId = normalizedMilestoneId,
                request = SubmitMilestoneSubmissionRequestDto(repoUrl = normalizedRepoUrl)
            )
        }.toDomainResult { response ->
            response.toDomain()
        }
    }

    override suspend fun getNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): Result<NodeQuiz> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedNodeId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }

        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getNodeQuiz(
                roadmapId = normalizedRoadmapId,
                nodeId = normalizedNodeId
            )
        }.toDomainResult { response ->
            response.toDomain()
        }
    }

    override suspend fun submitNodeQuiz(
        roadmapId: String,
        nodeId: String,
        answers: List<NodeQuizAnswer>
    ): Result<NodeQuizSubmissionResult> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedNodeId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }

        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.submitNodeQuiz(
                roadmapId = normalizedRoadmapId,
                nodeId = normalizedNodeId,
                request = answers.toSubmitQuizRequestDto()
            )
        }.toDomainResult { response ->
            response.toDomain()
        }
    }

    override suspend fun startRoadmap(roadmapId: String): Result<Unit> {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }

        return when (val result = getStartRoadmapResult(normalizedRoadmapId)) {
            is NetworkResult.Success -> {
                roadmapDetailCacheDao?.deleteById(normalizedRoadmapId)
                Result.success(Unit)
            }
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    override suspend fun resetRoadmapProgress(roadmapId: String): Result<Unit> {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }

        return when (val result = getResetRoadmapProgressResult(normalizedRoadmapId)) {
            is NetworkResult.Success -> {
                roadmapDetailCacheDao?.deleteById(normalizedRoadmapId)
                Result.success(Unit)
            }
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }

        return when (val result = getDeleteRoadmapResult(normalizedRoadmapId)) {
            is NetworkResult.Success -> {
                roadmapDetailCacheDao?.deleteById(normalizedRoadmapId)
                Result.success(Unit)
            }
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    override suspend fun updateNodeProgress(
        roadmapId: String,
        nodeId: String,
        status: LearningStatus
    ): Result<NodeProgressUpdateResult> {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        if (normalizedRoadmapId.isBlank()) {
            return Result.failure(invalidRoadmapId())
        }
        if (normalizedNodeId.isBlank()) {
            return Result.failure(invalidRoadmapNodeId())
        }

        return getUpdateNodeProgressResult(
            roadmapId = normalizedRoadmapId,
            nodeId = normalizedNodeId,
            status = status
        ).toDomainResult { response ->
            response.toDomain()
        }.onSuccess {
            roadmapDetailCacheDao?.deleteById(normalizedRoadmapId)
        }
    }

    private suspend fun loadPersonalRoadmapDetail(
        roadmap: RoadmapDto,
        roadmapId: String,
        statusCode: Int,
        nodesResult: NetworkResult<RoadmapNodesResponseDto>,
        progressResult: NetworkResult<RoadmapProgressDto>
    ): Result<RoadmapDetail> {
        return when {
            nodesResult is NetworkResult.Error -> Result.failure(nodesResult.toAppException())
            progressResult is NetworkResult.Error && progressResult.type != NetworkErrorType.NotFound ->
                Result.failure(progressResult.toAppException())

            nodesResult is NetworkResult.Success && progressResult is NetworkResult.Success -> {
                val detailDto = roadmap.toRoadmapWithNodes(nodesResult.data.nodes.orEmpty())
                progressResult.toDomainResult { progress ->
                    detailDto.toDomain(progress)
                }
            }

            nodesResult is NetworkResult.Success && progressResult is NetworkResult.Error -> {
                val detailDto = roadmap.toRoadmapWithNodes(nodesResult.data.nodes.orEmpty())
                runCatching {
                    detailDto.toDomain(RoadmapProgressDto(roadmapId = roadmapId))
                }.recoverCatching { error ->
                    throw invalidRoadmapResponse(statusCode, error)
                }
            }

            else -> Result.failure(invalidRoadmapResponse(statusCode))
        }
    }

    private suspend fun loadTemplateRoadmapDetail(
        templateResult: NetworkResult<RoadmapDto>,
        roadmapId: String
    ): Result<RoadmapDetail> {
        return when (templateResult) {
            is NetworkResult.Success -> {
                val nodesResult = getTemplateNodesResult(roadmapId)
                when (nodesResult) {
                    is NetworkResult.Success -> {
                        val detailDto = templateResult.data.toRoadmapWithNodes(nodesResult.data.nodes.orEmpty())
                        runCatching {
                            detailDto.toDomain(RoadmapProgressDto(roadmapId = roadmapId))
                        }.recoverCatching { error ->
                            throw invalidRoadmapResponse(templateResult.code, error)
                        }
                    }

                    is NetworkResult.Error -> Result.failure(nodesResult.toAppException())
                }
            }

            is NetworkResult.Error -> Result.failure(templateResult.toAppException())
        }
    }

    private suspend fun loadTemplates(hydrateNodes: Boolean): Result<List<RoadmapSummary>> {
        val result = getTemplatesResult(page = FIRST_PAGE, perPage = ROADMAP_PAGE_SIZE)

        return when (result) {
            is NetworkResult.Success -> loadTemplateSummaries(
                templates = result.data.data.orEmpty(),
                hydrateNodes = hydrateNodes,
                statusCode = result.code
            )

            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    private suspend fun loadTemplateSummaries(
        templates: List<RoadmapDto>,
        hydrateNodes: Boolean,
        statusCode: Int
    ): Result<List<RoadmapSummary>> {
        val summaries = mutableListOf<RoadmapSummary>()
        templates.forEach { roadmap ->
            val roadmapId = roadmap.id?.takeIf { it.isNotBlank() }
                ?: return Result.failure(invalidRoadmapResponse(statusCode))
            val nodes = if (hydrateNodes) {
                when (val nodesResult = getTemplateNodesResult(roadmapId)) {
                    is NetworkResult.Success -> nodesResult.data.nodes.orEmpty()
                    is NetworkResult.Error -> emptyList()
                }
            } else {
                emptyList()
            }
            val summary = runCatching { roadmap.toSummary(nodes) }
                .getOrElse { error ->
                    return Result.failure(invalidRoadmapResponse(statusCode, error))
                }
            summaries += summary
        }
        return Result.success(summaries)
    }

    private suspend fun getRoadmapsResult(
        page: Int,
        perPage: Int
    ) = SafeApiCall.execute(
        onUnauthorized = sessionManager::handleUnauthorized
    ) {
        roadmapApi.listUserRoadmaps(
            page = page,
            perPage = perPage
        )
    }

    private suspend fun getRoadmapResult(
        roadmapId: String
    ) = SafeApiCall.execute(
        onUnauthorized = sessionManager::handleUnauthorized
    ) {
        roadmapApi.getUserRoadmap(roadmapId)
    }

    private suspend fun getRoadmapNodesResult(
        roadmapId: String
    ): NetworkResult<RoadmapNodesResponseDto> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getUserRoadmapNodes(roadmapId)
        }
    }

    private suspend fun getRoadmapNodeDetailResult(
        roadmapId: String,
        nodeId: String
    ): NetworkResult<RoadmapNodeDetailResponseDto> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getRoadmapNodeDetail(
                roadmapId = roadmapId,
                nodeId = nodeId
            )
        }
    }

    private suspend fun getRoadmapProgressResult(
        roadmapId: String
    ): NetworkResult<RoadmapProgressDto> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.getUserRoadmapProgress(roadmapId)
        }
    }

    private suspend fun getStartRoadmapResult(
        roadmapId: String
    ): NetworkResult<Unit> {
        return SafeApiCall.executeUnit(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.startRoadmap(roadmapId)
        }
    }

    private suspend fun getResetRoadmapProgressResult(
        roadmapId: String
    ): NetworkResult<Unit> {
        return SafeApiCall.executeUnit(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.resetRoadmapProgress(roadmapId)
        }
    }

    private suspend fun getDeleteRoadmapResult(
        roadmapId: String
    ): NetworkResult<Unit> {
        return SafeApiCall.executeUnit(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.deleteRoadmap(roadmapId)
        }
    }

    private suspend fun getUpdateNodeProgressResult(
        roadmapId: String,
        nodeId: String,
        status: LearningStatus
    ): NetworkResult<UpdateNodeProgressResponseDto> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            roadmapApi.updateNodeProgress(
                roadmapId = roadmapId,
                nodeId = nodeId,
                request = UpdateNodeProgressRequestDto(
                    status = status.toNodeStatusRequestValue()
                )
            )
        }
    }

    private suspend fun getTemplatesResult(
        categoryId: String? = null,
        page: Int,
        perPage: Int
    ): NetworkResult<RoadmapsResponseDto> {
        val officialResult = SafeApiCall.execute {
            roadmapApi.listTemplates(
                roleCategory = categoryId,
                page = page,
                perPage = perPage
            )
        }

        return if (officialResult is NetworkResult.Error && officialResult.type == NetworkErrorType.NotFound) {
            SafeApiCall.execute {
                roadmapApi.listLegacyTemplates(
                    roleCategory = categoryId,
                    page = page,
                    perPage = perPage
                )
            }
        } else {
            officialResult
        }
    }

    private suspend fun getTemplateResult(
        templateId: String
    ) = SafeApiCall.execute {
        roadmapApi.getTemplate(templateId)
    }

    private suspend fun getTemplateNodesResult(
        templateId: String
    ): NetworkResult<RoadmapNodesResponseDto> {
        return SafeApiCall.execute {
            roadmapApi.getTemplateNodes(templateId)
        }
    }

    private inline fun <T, R> NetworkResult<T>.toDomainResult(
        mapper: (T) -> R
    ): Result<R> {
        return when (this) {
            is NetworkResult.Success -> runCatching { mapper(data) }
                .recoverCatching { error ->
                    throw invalidRoadmapResponse(code, error)
                }

            is NetworkResult.Error -> Result.failure(toAppException())
        }
    }

    private fun invalidRoadmapResponse(
        statusCode: Int,
        cause: Throwable? = null
    ): AppException {
        return AppException(
            message = "Unable to read roadmap response. Please try again.",
            code = statusCode,
            type = NetworkErrorType.Serialization,
            cause = cause
        )
    }

    private fun invalidRoadmapId(): AppException {
        return AppException(
            message = "Roadmap id is required.",
            type = NetworkErrorType.Unknown
        )
    }

    private fun invalidRoadmapNodeId(): AppException {
        return AppException(
            message = "Roadmap node id is required.",
            type = NetworkErrorType.Unknown
        )
    }

    private fun invalidRoadmapSkillId(): AppException {
        return AppException(
            message = "Skill id is required.",
            type = NetworkErrorType.Unknown
        )
    }

    private fun invalidMilestoneRepoUrl(): AppException {
        return AppException(
            message = "GitHub repository URL is required.",
            type = NetworkErrorType.Unknown
        )
    }

    private fun emptyLearningProgress(): LearningProgress {
        return LearningProgress(
            completedLessons = 0,
            totalLessons = 0,
            streakDays = 0,
            todayGoalCompleted = 0,
            todayGoalTotal = 0,
            completedRoadmaps = 0
        )
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val FIRST_ITEM_PAGE_SIZE = 1
        const val ROADMAP_PAGE_SIZE = 20
        const val TEMPLATE_SYNC_PAGE_SIZE = 100
        const val RECOMMENDED_ROADMAP_LIMIT = 5
    }
}
