package com.rmap.mobile.features.bookmarks.data.mapper

import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId

fun RoadmapSummary.toBookmark(
    entity: RoadmapBookmarkEntity
): RoadmapBookmark {
    return RoadmapBookmark(
        summary = this,
        status = toBookmarkStatus(),
        savedAtMillis = entity.savedAtMillis,
        updatedAtMillis = entity.updatedAtMillis
    )
}

fun RoadmapBookmarkEntity.toBookmarkFromSnapshot(): RoadmapBookmark? {
    val snapshotTitle = title?.takeIf { it.isNotBlank() } ?: return null
    val snapshotCategoryId = categoryId?.takeIf { it.isNotBlank() } ?: return null
    val summary = RoadmapSummary(
        id = roadmapId,
        title = snapshotTitle,
        totalLessonsCount = nodesTotal ?: 0,
        completedLessonsCount = 0,
        difficulty = LearningDifficulty.Beginner,
        durationLabel = durationLabel?.takeIf { it.isNotBlank() } ?: "Self-paced",
        icon = iconKey.toLearningTopicIcon(),
        categoryId = snapshotCategoryId,
        skillNodesCount = nodesTotal ?: 0
    )
    return RoadmapBookmark(
        summary = summary,
        status = LearningStatus.NotStarted,
        savedAtMillis = savedAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun RoadmapSummary.toBookmarkStatus(): LearningStatus {
    return when {
        totalLessonsCount > 0 && completedLessonsCount >= totalLessonsCount -> LearningStatus.Completed
        completedLessonsCount > 0 -> LearningStatus.InProgress
        else -> LearningStatus.NotStarted
    }
}

fun SkillBookmarkEntity.toDomain(
    roadmapDetail: RoadmapDetail
): SkillBookmark? {
    val skill = roadmapDetail.findSkill(skillId) ?: return null

    return SkillBookmark(
        title = skill.title,
        parentPathName = roadmapDetail.title,
        status = skill.status.toBookmarkStatus(),
        icon = skill.icon,
        skillId = skillId,
        roadmapId = roadmapId,
        savedAtMillis = savedAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

private fun LearningStatus.toBookmarkStatus(): LearningStatus {
    return when (this) {
        LearningStatus.Completed -> LearningStatus.Completed
        LearningStatus.InProgress -> LearningStatus.InProgress
        LearningStatus.Locked,
        LearningStatus.NotStarted -> LearningStatus.NotStarted
    }
}

fun newRoadmapBookmarkEntity(
    roadmapId: String,
    existingEntity: RoadmapBookmarkEntity?,
    nowMillis: Long
): RoadmapBookmarkEntity {
    return RoadmapBookmarkEntity(
        roadmapId = roadmapId,
        savedAtMillis = existingEntity?.savedAtMillis ?: nowMillis,
        updatedAtMillis = nowMillis,
        title = existingEntity?.title,
        categoryId = existingEntity?.categoryId,
        categoryLabel = existingEntity?.categoryLabel,
        nodesTotal = existingEntity?.nodesTotal,
        durationLabel = existingEntity?.durationLabel,
        iconKey = existingEntity?.iconKey
    )
}

fun RoadmapBookmarkSnapshot.toEntity(
    existingEntity: RoadmapBookmarkEntity?,
    nowMillis: Long
): RoadmapBookmarkEntity {
    return RoadmapBookmarkEntity(
        roadmapId = roadmapId,
        savedAtMillis = existingEntity?.savedAtMillis ?: nowMillis,
        updatedAtMillis = nowMillis,
        title = title,
        categoryId = categoryId,
        categoryLabel = categoryLabel,
        nodesTotal = nodesTotal,
        durationLabel = durationLabel,
        iconKey = iconKey
    )
}

fun newSkillBookmarkEntity(
    skillId: String,
    roadmapId: String,
    existingEntity: SkillBookmarkEntity?,
    nowMillis: Long
): SkillBookmarkEntity {
    return SkillBookmarkEntity(
        skillId = skillId,
        roadmapId = roadmapId,
        savedAtMillis = existingEntity?.savedAtMillis ?: nowMillis,
        updatedAtMillis = nowMillis
    )
}

private fun RoadmapDetail.findSkill(skillId: String): SkillBookmarkSource? {
    sections.forEach { section ->
        section.modules.forEach { module ->
            if (module.toStableId() == skillId) {
                return SkillBookmarkSource(
                    title = module.title,
                    status = module.status,
                    icon = module.icon
                )
            }

            module.subLessons.forEach { subLesson ->
                if (subLesson.toStableId() == skillId) {
                    return SkillBookmarkSource(
                        title = subLesson.title,
                        status = subLesson.status,
                        icon = module.icon
                    )
                }
            }
        }
    }

    return null
}

private fun LearningModule.toStableId(): String = toStableLearningId()

private fun SubLesson.toStableId(): String = toStableLearningId()

private fun String?.toLearningTopicIcon(): LearningTopicIcon {
    return when (this) {
        LearningTopicIcon.Code.name -> LearningTopicIcon.Code
        LearningTopicIcon.DataObject.name -> LearningTopicIcon.DataObject
        LearningTopicIcon.Devices.name -> LearningTopicIcon.Devices
        LearningTopicIcon.Game.name -> LearningTopicIcon.Game
        LearningTopicIcon.Palette.name -> LearningTopicIcon.Palette
        LearningTopicIcon.Science.name -> LearningTopicIcon.Science
        LearningTopicIcon.Security.name -> LearningTopicIcon.Security
        LearningTopicIcon.SmartToy.name -> LearningTopicIcon.SmartToy
        LearningTopicIcon.Storage.name -> LearningTopicIcon.Storage
        LearningTopicIcon.Terminal.name -> LearningTopicIcon.Terminal
        else -> LearningTopicIcon.Code
    }
}

private data class SkillBookmarkSource(
    val title: String,
    val status: LearningStatus,
    val icon: LearningTopicIcon
)
