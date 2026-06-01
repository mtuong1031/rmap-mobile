package com.rmap.mobile.features.roadmap.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RoadmapCategoryVisualsTest {
    @Test
    fun `maps backend category ids to compact display labels`() {
        assertEquals("Web", "WEB_DEVELOPMENT".toRoadmapCategoryDisplayLabel("Web Development"))
        assertEquals("Beginner", "ABSOLUTE_BEGINNERS".toRoadmapCategoryDisplayLabel("Absolute Beginners"))
        assertEquals("Languages", "LANGUAGES_AND_PLATFORMS".toRoadmapCategoryDisplayLabel("Languages And Platforms"))
        assertEquals("CS", "COMPUTER_SCIENCE".toRoadmapCategoryDisplayLabel("Computer Science"))
        assertEquals("AI", "AI_AND_MACHINE_LEARNING".toRoadmapCategoryDisplayLabel("Ai And Machine Learning"))
        assertEquals("Mobile", "MOBILE_DEVELOPMENT".toRoadmapCategoryDisplayLabel("Mobile Development"))
        assertEquals("Game", "GAME_DEVELOPMENT".toRoadmapCategoryDisplayLabel("Game Development"))
        assertEquals("Security", "CYBER_SECURITY".toRoadmapCategoryDisplayLabel("Cyber Security"))
    }

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
