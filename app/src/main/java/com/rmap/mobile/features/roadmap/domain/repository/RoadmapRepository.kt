package com.rmap.mobile.features.roadmap.domain.repository

import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent

interface RoadmapRepository {
    suspend fun getLearningProgress(): Result<LearningProgress>
    suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>>
    suspend fun getExploreCategories(): Result<List<RoadmapCategory>>
    suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>>
    suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>>
    suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail>
    suspend fun getLearningNode(roadmapId: String, nodeId: String): Result<LearningNodeDetail>
    suspend fun getNodeQuiz(roadmapId: String, nodeId: String): Result<NodeQuiz>
    suspend fun submitNodeQuiz(
        roadmapId: String,
        nodeId: String,
        answers: List<NodeQuizAnswer>
    ): Result<NodeQuizSubmissionResult>
    suspend fun getRoadmapNodeLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String
    ): Result<SkillLearningContent>
    suspend fun startRoadmap(roadmapId: String): Result<Unit>
    suspend fun updateNodeProgress(
        roadmapId: String,
        nodeId: String,
        status: LearningStatus
    ): Result<NodeProgressUpdateResult>
}
