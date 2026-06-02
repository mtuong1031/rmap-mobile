package com.rmap.mobile.features.roadmap.domain.repository

import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary

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
}
