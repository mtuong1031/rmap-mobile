package com.rmap.mobile.features.roadmap.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RoadmapCategoryVisualsTest {
    @Test
    fun `maps backend category ids to browse compact labels`() {
        assertEquals("Web", "WEB_DEVELOPMENT".toHomeBrowseCategoryLabel("Web Development"))
        assertEquals("Beginner", "ABSOLUTE_BEGINNERS".toHomeBrowseCategoryLabel("Absolute Beginners"))
        assertEquals("Languages", "LANGUAGES_AND_PLATFORMS".toHomeBrowseCategoryLabel("Languages And Platforms"))
        assertEquals("CS", "COMPUTER_SCIENCE".toHomeBrowseCategoryLabel("Computer Science"))
        assertEquals("AI", "AI_AND_MACHINE_LEARNING".toHomeBrowseCategoryLabel("Ai And Machine Learning"))
        assertEquals("Mobile", "MOBILE_DEVELOPMENT".toHomeBrowseCategoryLabel("Mobile Development"))
        assertEquals("Game", "GAME_DEVELOPMENT".toHomeBrowseCategoryLabel("Game Development"))
        assertEquals("Security", "CYBER_SECURITY".toHomeBrowseCategoryLabel("Cyber Security"))
    }

    @Test
    fun `display label keeps backend label outside browse categories`() {
        assertEquals("Web Development", "WEB_DEVELOPMENT".toRoadmapCategoryDisplayLabel("Web Development"))
        assertEquals(
            "Languages And Platforms",
            "LANGUAGES_AND_PLATFORMS".toRoadmapCategoryDisplayLabel("Languages And Platforms")
        )
        assertEquals("Computer Science", "COMPUTER_SCIENCE".toRoadmapCategoryDisplayLabel("Computer Science"))
        assertEquals(
            "Ai And Machine Learning",
            "AI_AND_MACHINE_LEARNING".toRoadmapCategoryDisplayLabel("Ai And Machine Learning")
        )
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
