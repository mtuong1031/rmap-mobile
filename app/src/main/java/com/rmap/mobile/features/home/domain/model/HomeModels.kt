package com.rmap.mobile.features.home.domain.model

data class HomeContent(
    val activeRoadmaps: List<HomeActiveRoadmap>,
    val metrics: HomeMetrics,
    val recommendations: List<HomeTemplateRoadmap>,
    val beginners: List<HomeTrendingRoadmap>,
    val trendings: List<HomeTrendingRoadmap>
)

data class HomeActiveRoadmap(
    val roadmapId: String,
    val title: String,
    val goalName: String?,
    val roleCategory: String,
    val startedAt: String,
    val currentGroup: HomeRoadmapGroup?,
    val planNode: HomePlanNode?,
    val chapter: HomeRoadmapChapter?,
    val progress: HomeRoadmapProgress,
    val nextUnlock: HomeNextUnlock?,
    val paceWarning: HomePaceWarning?
)

data class HomeRoadmapGroup(
    val id: String,
    val name: String
)

data class HomePlanNode(
    val id: String,
    val name: String,
    val description: String?,
    val nodeType: String,
    val estimatedHours: Double?
)

data class HomeRoadmapChapter(
    val current: Int,
    val total: Int,
    val label: String
)

data class HomeRoadmapProgress(
    val requiredNodesCompleted: Int,
    val requiredNodesTotal: Int,
    val requiredCompletionPct: Double
)

data class HomeNextUnlock(
    val id: String,
    val name: String
)

data class HomePaceWarning(
    val isBehind: Boolean,
    val paceDeficitPct: Double,
    val estimatedDelayDays: Int,
    val message: String,
    val title: String,
    val actionLabel: String
)

data class HomeMetrics(
    val roadmapCompletionPct: Double,
    val streakDays: Int,
    val readinessPct: Double
)

data class HomeSearchResult(
    val query: String,
    val roadmaps: HomeSearchRoadmapsPage,
    val skills: HomeSearchSkillsPage,
    val totalResults: Int,
    val roadmapPageSize: Int,
    val skillPageSize: Int
)

data class HomeSearchRoadmapsPage(
    val data: List<HomeSearchRoadmap>,
    val meta: HomeSearchPageMeta
)

data class HomeSearchSkillsPage(
    val data: List<HomeSearchSkill>,
    val meta: HomeSearchPageMeta
)

data class HomeSearchRoadmap(
    val roadmapId: String,
    val title: String,
    val description: String?,
    val goalName: String?,
    val isTemplate: Boolean,
    val roadmapType: String,
    val roleCategory: String,
    val categoryLabel: String,
    val estimatedWeeks: Int?,
    val durationLabel: String?
)

data class HomeSearchSkill(
    val skillId: String,
    val name: String,
    val description: String?,
    val roleCategory: String,
    val categoryLabel: String,
    val defaultEstimatedHours: Int?
)

data class HomeSearchPageMeta(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int
) {
    val hasNextPage: Boolean
        get() = page < totalPages
}

data class HomeTemplateRoadmap(
    val roadmapId: String,
    val title: String,
    val description: String?,
    val goalName: String?,
    val roleCategory: String,
    val categoryLabel: String,
    val estimatedWeeks: Int?,
    val durationLabel: String?,
    val nodesTotal: Int,
    val requiredNodesTotal: Int
)

data class HomeTrendingRoadmap(
    val rank: Int,
    val roadmapId: String,
    val title: String,
    val roleCategory: String,
    val categoryLabel: String,
    val estimatedWeeks: Int?,
    val durationLabel: String?,
    val nodesTotal: Int,
    val trendText: String
)
