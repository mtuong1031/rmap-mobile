package com.rmap.mobile.features.roadmap.data

import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

class FakeRoadmapRepository : RoadmapRepository {
    private val categories = listOf(
        RoadmapCategory("frontend", "Frontend", LearningTopicIcon.Code),
        RoadmapCategory("backend", "Backend", LearningTopicIcon.Storage),
        RoadmapCategory("mobile", "Mobile", LearningTopicIcon.Devices),
        RoadmapCategory("devops", "DevOps", LearningTopicIcon.Terminal),
        RoadmapCategory("ai", "AI", LearningTopicIcon.SmartToy)
    )

    private val roadmaps = listOf(
        RoadmapSummary("frontend-pro", "Frontend Pro", 120, 1, LearningDifficulty.Expert, "3 months", LearningTopicIcon.Code, "frontend", "MOST POPULAR", 48),
        RoadmapSummary("devops-specialist", "DevOps Specialist", 185, 0, LearningDifficulty.Beginner, "6 months", LearningTopicIcon.DataObject, "devops"),
        RoadmapSummary("ui-ux-master", "UI/UX Master", 96, 0, LearningDifficulty.Intermediate, "2 months", LearningTopicIcon.Palette, "frontend"),
        RoadmapSummary("data-science", "Data Science", 240, 0, LearningDifficulty.Hard, "4 months", LearningTopicIcon.Science, "ai"),
        RoadmapSummary("ai-engineering", "AI Engineering Path", 64, 0, LearningDifficulty.Advanced, "4 months", LearningTopicIcon.SmartToy, "ai", "CAREER PATH", 64),
        RoadmapSummary("full-stack-development", "Full Stack Development", 160, 24, LearningDifficulty.Intermediate, "8 months", LearningTopicIcon.Code, "backend", coverPlaceholder = RoadmapCoverPlaceholder.FullStack),
        RoadmapSummary("ui-ux-masterclass", "UI/UX Masterclass", 72, 0, LearningDifficulty.Beginner, "4 months", LearningTopicIcon.Palette, "frontend", coverPlaceholder = RoadmapCoverPlaceholder.UiUx)
    )

    private val details = mapOf(
        "frontend-pro" to RoadmapDetail(
            id = "frontend-pro",
            title = "Frontend Pro",
            completedLessons = 6,
            totalLessons = 8,
            sections = listOf(
                LearningModuleSection(
                    title = "Core Web Fundamentals",
                    modules = listOf(
                        LearningModule(
                            title = "HTML & CSS",
                            status = LearningStatus.Completed,
                            progressPercent = 100,
                            icon = LearningTopicIcon.Code,
                            subLessons = listOf(
                                SubLesson("Semantic HTML", LearningStatus.Completed),
                                SubLesson("CSS Flexbox & Grid", LearningStatus.Completed),
                                SubLesson("Responsive Design", LearningStatus.Completed)
                            )
                        ),
                        LearningModule(
                            title = "JavaScript Basics",
                            status = LearningStatus.InProgress,
                            progressPercent = 45,
                            icon = LearningTopicIcon.DataObject,
                            subLessons = listOf(
                                SubLesson("ES6+ Syntax", LearningStatus.Completed),
                                SubLesson("Asynchronous JS", LearningStatus.InProgress),
                                SubLesson("DOM Manipulation", LearningStatus.Locked)
                            )
                        )
                    )
                ),
                LearningModuleSection(
                    title = "Framework Ecosystem",
                    modules = listOf(
                        LearningModule("React Fundamentals", LearningStatus.Locked, 0, LearningTopicIcon.Storage, emptyList())
                    )
                )
            ),
            aiTip = AiScholarTip("Asynchronous JS", "Promises", "DOM Manipulation")
        )
    )

    override suspend fun getLearningProgress(): Result<LearningProgress> = Result.success(
        LearningProgress(
            completedLessons = 1,
            totalLessons = 107,
            streakDays = 2,
            todayGoalCompleted = 1,
            todayGoalTotal = 3,
            completedRoadmaps = 1
        )
    )

    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> = Result.success(roadmaps.take(4))

    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> = Result.success(categories)

    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> =
        Result.success(roadmaps.filter { it.recommendationBadge != null })

    override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
        val normalizedQuery = query.trim()
        val result = if (normalizedQuery.isBlank()) {
            roadmaps.take(4)
        } else {
            roadmaps.filter { roadmap ->
                roadmap.title.contains(normalizedQuery, ignoreCase = true) ||
                    roadmap.categoryId.contains(normalizedQuery, ignoreCase = true)
            }
        }
        return Result.success(result)
    }

    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
        return details[id]?.let { detail -> Result.success(detail) }
            ?: Result.failure(IllegalArgumentException("Roadmap not found"))
    }
}
