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

enum class LearningRequirement {
    Required,
    Optional
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
    val categoryLabel: String = "",
    val completedLessons: Int,
    val totalLessons: Int,
    val sections: List<LearningModuleSection>,
    val milestones: List<RoadmapMilestone> = emptyList(),
    val contentItems: List<RoadmapContentItem> = emptyList(),
    val aiTip: AiScholarTip?,
    val roleId: String = "",
    val roleName: String = "",
    val description: String? = null,
    val isTemplate: Boolean = false,
    val hasStartedLearning: Boolean = false
) {
    val progressFraction: Float
        get() = if (totalLessons <= 0) {
            0f
        } else {
            (completedLessons.toFloat() / totalLessons.toFloat()).coerceIn(0f, 1f)
        }
}

sealed class RoadmapContentItem {
    data class Group(val section: LearningModuleSection) : RoadmapContentItem()
    data class Milestone(val milestone: RoadmapMilestone) : RoadmapContentItem()
}

data class NodeProgressUpdateResult(
    val nodeId: String,
    val status: LearningStatus,
    val unlockedNodeIds: List<String>
)

data class RoadmapMilestone(
    val id: String,
    val title: String,
    val description: String?,
    val status: LearningStatus
)

fun String.toStableLearningId(): String {
    return trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

fun RoadmapDetail.containsLearningItem(skillId: String): Boolean {
    return sections.any { section ->
        section.modules.any { module ->
            module.skillId == skillId ||
                module.id == skillId ||
                module.toStableLearningId() == skillId ||
                module.subLessons.any { subLesson ->
                    subLesson.skillId == skillId ||
                        subLesson.id == skillId ||
                        subLesson.toStableLearningId() == skillId
                }
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
    val subLessons: List<SubLesson>,
    val id: String = title.toStableLearningId(),
    val skillId: String = id,
    val requirement: LearningRequirement = LearningRequirement.Required,
    val estimatedHours: Int? = null,
    val resourcesCount: Int = 0,
    val description: String? = null,
    val quizScorePercent: Int? = null,
    val quizPassed: Boolean? = null
)

data class SubLesson(
    val title: String,
    val status: LearningStatus,
    val id: String = title.toStableLearningId(),
    val skillId: String = id,
    val requirement: LearningRequirement = LearningRequirement.Required,
    val estimatedHours: Int? = null,
    val resourcesCount: Int = 0,
    val description: String? = null
)

fun LearningModule.toStableLearningId(): String = id.ifBlank { title.toStableLearningId() }

fun SubLesson.toStableLearningId(): String = id.ifBlank { title.toStableLearningId() }

data class AiScholarTip(
    val currentModule: String,
    val recommendedTopic: String,
    val nextModule: String
)

data class SkillLearningContent(
    val skill: SkillDetail,
    val resources: List<SkillResource>,
    val status: LearningStatus? = null,
    val quizPassed: Boolean = false
) {
    val isCompleted: Boolean
        get() = status == LearningStatus.Completed

    val canMarkCompleted: Boolean
        get() = status == LearningStatus.InProgress && quizPassed
}

data class SkillDetail(
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val estimatedHours: Int?
)

data class SkillResource(
    val id: String,
    val skillId: String,
    val title: String,
    val url: String,
    val platform: SkillResourcePlatform,
    val isFree: Boolean,
    val levelTag: SkillLevelTag?
)

enum class SkillResourcePlatform {
    Udemy,
    Coursera,
    Youtube,
    Other
}

enum class SkillLevelTag {
    Fresher,
    Junior,
    Middle
}
