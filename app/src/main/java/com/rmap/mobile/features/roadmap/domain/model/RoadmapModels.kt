package com.rmap.mobile.features.roadmap.domain.model

enum class LearningDifficulty {
    Beginner,
    Intermediate,
    Advanced,
    Expert,
    Hard
}

enum class LearningStatus {
    Completed,
    InProgress,
    Locked,
    NotStarted
}

enum class LearningTopicIcon {
    Code,
    DataObject,
    Devices,
    Game,
    Palette,
    Science,
    Security,
    SmartToy,
    Storage,
    Terminal
}

data class LearningProgress(
    val completedLessons: Int,
    val totalLessons: Int,
    val streakDays: Int,
    val todayGoalCompleted: Int,
    val todayGoalTotal: Int,
    val completedRoadmaps: Int
) {
    val remainingLessons: Int
        get() = (totalLessons - completedLessons).coerceAtLeast(0)

    val progressFraction: Float
        get() = if (totalLessons <= 0) {
            0f
        } else {
            (completedLessons.toFloat() / totalLessons.toFloat()).coerceIn(0f, 1f)
        }
}

data class RoadmapCategory(
    val id: String,
    val name: String,
    val icon: LearningTopicIcon
)

data class RoadmapSummary(
    val id: String,
    val title: String,
    val totalLessonsCount: Int,
    val completedLessonsCount: Int,
    val difficulty: LearningDifficulty,
    val durationLabel: String,
    val icon: LearningTopicIcon,
    val categoryId: String,
    val recommendationBadge: String? = null,
    val skillNodesCount: Int = totalLessonsCount,
    val coverPlaceholder: RoadmapCoverPlaceholder? = null
)

enum class RoadmapCoverPlaceholder {
    FullStack,
    UiUx
}

data class RoadmapDetail(
    val id: String,
    val title: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val sections: List<LearningModuleSection>,
    val aiTip: AiScholarTip?
) {
    val progressFraction: Float
        get() = if (totalLessons <= 0) {
            0f
        } else {
            (completedLessons.toFloat() / totalLessons.toFloat()).coerceIn(0f, 1f)
        }
}

fun String.toStableLearningId(): String {
    return trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

fun RoadmapDetail.containsLearningItem(skillId: String): Boolean {
    return sections.any { section ->
        section.modules.any { module ->
            module.toStableLearningId() == skillId ||
                module.subLessons.any { subLesson -> subLesson.toStableLearningId() == skillId }
        }
    }
}

data class LearningModuleSection(
    val title: String,
    val modules: List<LearningModule>
)

data class LearningModule(
    val title: String,
    val status: LearningStatus,
    val progressPercent: Int,
    val icon: LearningTopicIcon,
    val subLessons: List<SubLesson>
)

data class SubLesson(
    val title: String,
    val status: LearningStatus
)

fun LearningModule.toStableLearningId(): String = title.toStableLearningId()

fun SubLesson.toStableLearningId(): String = title.toStableLearningId()

data class AiScholarTip(
    val currentModule: String,
    val recommendedTopic: String,
    val nextModule: String
)
