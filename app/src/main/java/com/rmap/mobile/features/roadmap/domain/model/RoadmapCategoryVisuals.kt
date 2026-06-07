package com.rmap.mobile.features.roadmap.domain.model

import com.rmap.mobile.core.domain.model.RMapCategoryIconKey
import com.rmap.mobile.core.domain.model.toRMapCategoryIconKey

fun String.toRoadmapCategoryIcon(): LearningTopicIcon {
    return toRMapCategoryIconKey().toLearningTopicIcon()
}

fun RMapCategoryIconKey.toLearningTopicIcon(): LearningTopicIcon {
    return when (this) {
        RMapCategoryIconKey.Code -> LearningTopicIcon.Code
        RMapCategoryIconKey.DataObject -> LearningTopicIcon.DataObject
        RMapCategoryIconKey.Devices -> LearningTopicIcon.Devices
        RMapCategoryIconKey.Game -> LearningTopicIcon.Game
        RMapCategoryIconKey.Palette -> LearningTopicIcon.Palette
        RMapCategoryIconKey.Science -> LearningTopicIcon.Science
        RMapCategoryIconKey.Security -> LearningTopicIcon.Security
        RMapCategoryIconKey.SmartToy -> LearningTopicIcon.SmartToy
        RMapCategoryIconKey.Storage -> LearningTopicIcon.Storage
        RMapCategoryIconKey.Terminal -> LearningTopicIcon.Terminal
    }
}
