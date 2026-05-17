package com.rmap.mobile.features.bookmarks.data

import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary

class FakeBookmarkRepository : BookmarkRepository {
    override suspend fun getSavedRoadmaps(): Result<List<RoadmapSummary>> {
        return Result.success(
            listOf(
                RoadmapSummary(
                    id = "frontend-pro",
                    title = "Frontend Pro",
                    totalLessonsCount = 120,
                    completedLessonsCount = 1,
                    difficulty = LearningDifficulty.Intermediate,
                    durationLabel = "6 months",
                    icon = LearningTopicIcon.Code,
                    categoryId = "frontend",
                    coverPlaceholder = RoadmapCoverPlaceholder.FullStack
                ),
                RoadmapSummary(
                    id = "full-stack-development",
                    title = "Full Stack Development",
                    totalLessonsCount = 160,
                    completedLessonsCount = 24,
                    difficulty = LearningDifficulty.Intermediate,
                    durationLabel = "8 months",
                    icon = LearningTopicIcon.Code,
                    categoryId = "backend",
                    coverPlaceholder = RoadmapCoverPlaceholder.FullStack
                ),
                RoadmapSummary(
                    id = "ui-ux-masterclass",
                    title = "UI/UX Masterclass",
                    totalLessonsCount = 72,
                    completedLessonsCount = 0,
                    difficulty = LearningDifficulty.Beginner,
                    durationLabel = "4 months",
                    icon = LearningTopicIcon.Palette,
                    categoryId = "frontend",
                    coverPlaceholder = RoadmapCoverPlaceholder.UiUx
                )
            )
        )
    }

    override suspend fun getSavedSkills(): Result<List<SkillBookmark>> {
        return Result.success(
            listOf(
                SkillBookmark(
                    title = "Advanced CSS Layouts",
                    parentPathName = "Frontend Dev",
                    status = LearningStatus.InProgress,
                    icon = LearningTopicIcon.Code
                ),
                SkillBookmark(
                    title = "NoSQL Data Modeling",
                    parentPathName = "Backend Systems",
                    status = LearningStatus.NotStarted,
                    icon = LearningTopicIcon.DataObject
                )
            )
        )
    }
}
