package com.rmap.mobile.features.roadmap.domain.repository

import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
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
}
