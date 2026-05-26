package com.rmap.mobile.features.airoadmap.domain.repository

import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import kotlinx.coroutines.flow.StateFlow

interface AiRoadmapRepository {
    val generationStatus: StateFlow<AiRoadmapGenerationStatus>

    suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<List<AiRoadmapQuestion>>

    suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest>

    fun startGeneration(request: AiRoadmapGenerationRequest)

    fun cancelGeneration()
}
