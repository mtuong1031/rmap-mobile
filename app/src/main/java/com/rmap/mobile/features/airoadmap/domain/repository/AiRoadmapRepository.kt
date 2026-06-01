package com.rmap.mobile.features.airoadmap.domain.repository

import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuizResult
import kotlinx.coroutines.flow.StateFlow

interface AiRoadmapRepository {
    val generationStatus: StateFlow<AiRoadmapGenerationStatus>

    suspend fun getGeneratedRoadmaps(): Result<List<AiGeneratedRoadmap>>

    suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<AiRoadmapQuizResult>

    suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest>

    fun startGeneration(request: AiRoadmapGenerationRequest)

    fun cancelGeneration()
}
