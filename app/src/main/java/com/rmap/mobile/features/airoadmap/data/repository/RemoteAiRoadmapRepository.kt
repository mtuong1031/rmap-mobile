package com.rmap.mobile.features.airoadmap.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.airoadmap.data.AiRoadmapGenerationWorker
import com.rmap.mobile.features.airoadmap.data.local.dao.AiRoadmapCacheDao
import com.rmap.mobile.features.airoadmap.data.local.mapper.toAiGeneratedRoadmaps
import com.rmap.mobile.features.airoadmap.data.local.mapper.toAiRoadmapCacheEntity
import com.rmap.mobile.features.airoadmap.data.mapper.toDomain
import com.rmap.mobile.features.airoadmap.data.model.OnboardingQuizRequestDto
import com.rmap.mobile.features.airoadmap.data.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.airoadmap.data.remote.AiRoadmapApi
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuizResult
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteAiRoadmapRepository(
    context: Context,
    private val api: AiRoadmapApi,
    private val sessionManager: SessionManager,
    private val aiRoadmapCacheDao: AiRoadmapCacheDao? = null,
    private val gson: Gson = Gson()
) : AiRoadmapRepository {
    private val workManager = WorkManager.getInstance(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _generationStatus = MutableStateFlow(AiRoadmapGenerationStatus())

    override val generationStatus: StateFlow<AiRoadmapGenerationStatus> = _generationStatus.asStateFlow()

    init {
        scope.launch {
            while (true) {
                val workInfos = withContext(Dispatchers.IO) {
                    workManager.getWorkInfosForUniqueWork(AiRoadmapGenerationWorker.UNIQUE_WORK_NAME).get()
                }
                _generationStatus.value = workInfos.firstOrNull()?.toGenerationStatus()
                    ?: AiRoadmapGenerationStatus()
                delay(STATUS_POLL_INTERVAL_MILLIS)
            }
        }
    }

    override suspend fun getGeneratedRoadmaps(): Result<List<AiGeneratedRoadmap>> {
        val cachedRoadmaps = aiRoadmapCacheDao?.get()?.toAiGeneratedRoadmaps(gson)
        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            api.listRoadmaps(
                page = ROADMAP_LIST_PAGE,
                perPage = ROADMAP_LIST_PER_PAGE
            )
        }

        return when (networkResult) {
            is NetworkResult.Success -> {
                val roadmaps = networkResult.data.data.map { roadmap ->
                    roadmap.toDomain(lessonsCount = getNodeCountOrZero(roadmap.id))
                }
                aiRoadmapCacheDao?.upsert(roadmaps.toAiRoadmapCacheEntity(gson))
                Result.success(roadmaps)
            }

            is NetworkResult.Error -> {
                cachedRoadmaps
                    ?.let { roadmaps -> Result.success(roadmaps) }
                    ?: Result.failure(networkResult.toAppException())
            }
        }
    }

    override suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<AiRoadmapQuizResult> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            api.createQuiz(
                OnboardingQuizRequestDto(
                    topic = draft.topic
                )
            )
        }

        return networkResult.toDomainResult { response ->
            response.toDomain(goal = draft.topic)
        }
    }

    override suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest> {
        val roleCategory = draft.roleCategory
        return when {
            roleCategory.isNullOrBlank() -> Result.failure(
                IllegalArgumentException("Unable to determine roadmap role category.")
            )

            answers.size !in MIN_QUIZ_ANSWERS..MAX_QUIZ_ANSWERS -> Result.failure(
                IllegalArgumentException("Please answer every question before generating your roadmap.")
            )

            answers.any { !it.hasAnswer } -> Result.failure(
                IllegalArgumentException("Please answer every question before generating your roadmap.")
            )

            else -> Result.success(
                AiRoadmapGenerationRequest(
                    draft = draft.copy(roleCategory = roleCategory),
                    answers = answers
                )
            )
        }
    }

    override fun startGeneration(request: AiRoadmapGenerationRequest) {
        if (_generationStatus.value.isActive) return

        scope.launch {
            aiRoadmapCacheDao?.clear()
        }

        val workRequest = OneTimeWorkRequestBuilder<AiRoadmapGenerationWorker>()
            .setInputData(
                workDataOf(
                    AiRoadmapGenerationWorker.KEY_GOAL to request.draft.topic,
                    AiRoadmapGenerationWorker.KEY_ROLE_CATEGORY to request.draft.roleCategory.orEmpty(),
                    AiRoadmapGenerationWorker.KEY_DEADLINE_EPOCH_MILLIS to request.draft.deadlineEpochMillis,
                    AiRoadmapGenerationWorker.KEY_DAILY_STUDY_HOURS to request.draft.dailyStudyHours,
                    AiRoadmapGenerationWorker.KEY_QUIZ_QUESTIONS to request.answers.map { it.question }.toTypedArray(),
                    AiRoadmapGenerationWorker.KEY_QUIZ_ANSWERS to request.answers.map { it.answer }.toTypedArray()
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            AiRoadmapGenerationWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun cancelGeneration() {
        workManager.cancelUniqueWork(AiRoadmapGenerationWorker.UNIQUE_WORK_NAME)
    }

    private suspend fun getNodeCountOrZero(roadmapId: String): Int {
        val nodesResult = SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            api.listRoadmapNodes(roadmapId)
        }
        return nodesResult.nodeCountOrZero()
    }

    private fun NetworkResult<RoadmapNodesResponseDto>.nodeCountOrZero(): Int {
        return when (this) {
            is NetworkResult.Success -> data.nodes.count { node ->
                node.nodeType == NODE_TYPE_REQUIRED || node.nodeType == NODE_TYPE_OPTIONAL
            }

            is NetworkResult.Error -> 0
        }
    }

    private fun WorkInfo.toGenerationStatus(): AiRoadmapGenerationStatus {
        val progressPercent = progress.getInt(AiRoadmapGenerationWorker.KEY_PROGRESS, 0)
        val stage = progress.getString(AiRoadmapGenerationWorker.KEY_STAGE).orEmpty()

        return when (state) {
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.BLOCKED -> AiRoadmapGenerationStatus(
                phase = AiRoadmapGenerationPhase.Queued,
                progressPercent = progressPercent,
                stageLabel = stage
            )

            WorkInfo.State.RUNNING -> AiRoadmapGenerationStatus(
                phase = AiRoadmapGenerationPhase.Running,
                progressPercent = progressPercent,
                stageLabel = stage
            )

            WorkInfo.State.SUCCEEDED -> AiRoadmapGenerationStatus(
                phase = AiRoadmapGenerationPhase.Succeeded,
                progressPercent = 100,
                stageLabel = outputData.getString(AiRoadmapGenerationWorker.KEY_STAGE).orEmpty(),
                generatedRoadmapId = outputData.getString(AiRoadmapGenerationWorker.KEY_ROADMAP_ID)
            )

            WorkInfo.State.FAILED -> AiRoadmapGenerationStatus(
                phase = AiRoadmapGenerationPhase.Failed,
                progressPercent = progressPercent,
                stageLabel = stage,
                errorMessage = outputData.getString(AiRoadmapGenerationWorker.KEY_ERROR_MESSAGE)
            )

            WorkInfo.State.CANCELLED -> AiRoadmapGenerationStatus(
                phase = AiRoadmapGenerationPhase.Cancelled,
                progressPercent = progressPercent,
                stageLabel = stage
            )
        }
    }

    private companion object {
        const val STATUS_POLL_INTERVAL_MILLIS = 1_000L
        const val ROADMAP_LIST_PAGE = 1
        const val ROADMAP_LIST_PER_PAGE = 50
        const val MIN_QUIZ_ANSWERS = 7
        const val MAX_QUIZ_ANSWERS = 10
        const val NODE_TYPE_REQUIRED = "REQUIRED"
        const val NODE_TYPE_OPTIONAL = "OPTIONAL"
    }
}
