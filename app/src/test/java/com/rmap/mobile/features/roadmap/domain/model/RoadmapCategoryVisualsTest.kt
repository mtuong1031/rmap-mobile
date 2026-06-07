package com.rmap.mobile.features.roadmap.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RoadmapCategoryVisualsTest {
    @Test
    fun `maps backend category ids to shared topic icons`() {
        assertEquals(LearningTopicIcon.Code, "WEB_DEVELOPMENT".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.Terminal, "LANGUAGES_AND_PLATFORMS".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.Science, "COMPUTER_SCIENCE".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.SmartToy, "AI_AND_MACHINE_LEARNING".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.Devices, "MOBILE_DEVELOPMENT".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.Game, "GAME_DEVELOPMENT".toRoadmapCategoryIcon())
        assertEquals(LearningTopicIcon.Security, "CYBER_SECURITY".toRoadmapCategoryIcon())
    }
}
