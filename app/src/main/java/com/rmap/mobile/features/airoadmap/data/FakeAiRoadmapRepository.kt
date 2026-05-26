package com.rmap.mobile.features.airoadmap.data

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestionOption
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FakeAiRoadmapRepository(
    context: Context
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

    override suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<List<AiRoadmapQuestion>> {
        val topic = draft.topic.trim().ifBlank { return Result.failure(IllegalArgumentException("Topic is required")) }

        return Result.success(
            listOf(
                question(
                    id = "current-level",
                    skillName = topic,
                    prompt = "How would you describe your current level with $topic?",
                    options = listOf(
                        "I am completely new",
                        "I know the basics",
                        "I can build small projects",
                        "I have production experience"
                    )
                ),
                question(
                    id = "learning-style",
                    skillName = "Learning style",
                    prompt = "What learning format helps you move fastest?",
                    options = listOf(
                        "Short videos and examples",
                        "Official docs and notes",
                        "Hands-on mini projects",
                        "Quizzes and review tasks"
                    )
                ),
                question(
                    id = "project-goal",
                    skillName = "Practice goal",
                    prompt = "What do you want to be able to build by the deadline?",
                    options = listOf(
                        "A portfolio project",
                        "An interview-ready demo",
                        "A production-style app",
                        "A complete capstone project"
                    )
                ),
                question(
                    id = "weakest-area",
                    skillName = "Confidence check",
                    prompt = "Which area should RMap spend extra time on?",
                    options = listOf(
                        "Fundamentals",
                        "Architecture",
                        "Testing and debugging",
                        "Deployment and maintenance"
                    )
                )
            )
        )
    }

    override suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest> {
        return if (answers.all { it.hasAnswer }) {
            Result.success(AiRoadmapGenerationRequest(draft = draft, answers = answers))
        } else {
            Result.failure(IllegalArgumentException("Please answer every question before generating your roadmap."))
        }
    }

    override fun startGeneration(request: AiRoadmapGenerationRequest) {
        if (_generationStatus.value.isActive) return

        val workRequest = OneTimeWorkRequestBuilder<AiRoadmapGenerationWorker>()
            .setInputData(
                workDataOf(
                    AiRoadmapGenerationWorker.KEY_TOPIC to request.draft.topic,
                    AiRoadmapGenerationWorker.KEY_DEADLINE_EPOCH_MILLIS to request.draft.deadlineEpochMillis,
                    AiRoadmapGenerationWorker.KEY_DAILY_STUDY_HOURS to request.draft.dailyStudyHours
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

    private fun question(
        id: String,
        skillName: String,
        prompt: String,
        options: List<String>
    ): AiRoadmapQuestion {
        return AiRoadmapQuestion(
            id = id,
            skillName = skillName,
            prompt = prompt,
            options = options.mapIndexed { index, label ->
                AiRoadmapQuestionOption(
                    id = "$id-option-${index + 1}",
                    label = label
                )
            }
        )
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
    }
}
