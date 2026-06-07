package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.R
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.SkillLevelTag
import com.rmap.mobile.features.roadmap.domain.model.SkillResource
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform

fun SkillLearningContent.toRoadmapLearningUiState(
    roadmapId: String,
    nodeId: String,
    skillId: String,
    isCompleted: Boolean
): RoadmapLearningUiState {
    val resolvedCompleted = isCompleted || this.isCompleted
    val canMarkCompleted = !resolvedCompleted && this.canMarkCompleted
    val isNodeLocked = status == LearningStatus.Locked
    val canTakeQuiz = !resolvedCompleted && status == LearningStatus.InProgress

    return RoadmapLearningUiState(
        roadmapId = roadmapId,
        nodeId = nodeId,
        skillId = skillId,
        skill = skill.toUiModel(),
        resources = resources.map { resource -> resource.toUiModel() },
        isCompleted = resolvedCompleted,
        canTakeQuiz = canTakeQuiz,
        canMarkCompleted = canMarkCompleted,
        isNodeLocked = isNodeLocked,
        isLoading = false,
        isCompleting = false,
        completionBlockedMessageResId = if (resolvedCompleted || canMarkCompleted) {
            null
        } else {
            R.string.roadmap_learning_completion_requires_quiz
        },
        errorMessageResId = null
    )
}

private fun SkillDetail.toUiModel(): SkillLearningDetailUiModel {
    return SkillLearningDetailUiModel(
        id = id,
        name = name,
        description = description,
        category = category,
        estimatedHours = estimatedHours
    )
}

private fun SkillResource.toUiModel(): SkillLearningResourceUiModel {
    return SkillLearningResourceUiModel(
        id = id,
        title = title,
        url = url,
        platformLabelResId = platform.toLabelResId(),
        isFree = isFree,
        levelLabelResId = levelTag?.toLabelResId()
    )
}

private fun SkillResourcePlatform.toLabelResId(): Int {
    return when (this) {
        SkillResourcePlatform.Udemy -> R.string.roadmap_learning_platform_udemy
        SkillResourcePlatform.Coursera -> R.string.roadmap_learning_platform_coursera
        SkillResourcePlatform.Youtube -> R.string.roadmap_learning_platform_youtube
        SkillResourcePlatform.Other -> R.string.roadmap_learning_platform_other
    }
}

private fun SkillLevelTag.toLabelResId(): Int {
    return when (this) {
        SkillLevelTag.Fresher -> R.string.roadmap_learning_level_fresher
        SkillLevelTag.Junior -> R.string.roadmap_learning_level_junior
        SkillLevelTag.Middle -> R.string.roadmap_learning_level_middle
    }
}
