package com.rmap.mobile.features.myroadmap.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapCardUiModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapFilter
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapFilterUiModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapUiState
import com.rmap.mobile.navigation.NavBarDestination
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyRoadmapScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val defaultUiState = MyRoadmapUiState(
        roadmaps = listOf(
            MyRoadmapCardUiModel(
                id = "roadmap-1",
                title = "Backend Intern Roadmap",
                categoryKey = "WEB_DEVELOPMENT",
                categoryLabel = "Web Development",
                isTemplate = false,
                completionPercent = 50,
                nodesCompleted = 5,
                nodesTotal = 10,
                deadlineDate = null,
                estimatedWeeks = null,
                startedAt = "2026-06-01T00:00:00Z",
                isBehind = false
            )
        ),
        isLoading = false
    )

    @Test
    fun rendersFilterChipsAndRoadmapsWhenAuthenticated() {
        composeRule.setContent {
            RMapTheme(darkTheme = false, dynamicColor = false) {
                MyRoadmapScreen(
                    uiState = defaultUiState,
                    isAuthenticated = true,
                    selectedDestination = NavBarDestination.MyRoadmap,
                    onDestinationSelected = {},
                    onSearchQueryChange = {},
                    onClearSearchClick = {},
                    onFilterSelected = {},
                    onRoadmapClick = {},
                    onRoadmapCtaClick = {},
                    onRetryClick = {},
                    onCreateWithAiClick = {},
                    onExploreRoadmapsClick = {}
                )
            }
        }

        // Active filter chip should be displayed
        val activeFilterLabel = targetContext.getString(R.string.my_roadmap_filter_active)
        composeRule.onNodeWithText(activeFilterLabel).assertIsDisplayed()

        // Roadmap should be displayed
        composeRule.onNodeWithText("Backend Intern Roadmap").assertIsDisplayed()
    }

    @Test
    fun cardAndCtaClickCallbacksWork() {
        var roadmapClickedId: String? = null
        var ctaClickedId: String? = null

        composeRule.setContent {
            RMapTheme(darkTheme = false, dynamicColor = false) {
                MyRoadmapScreen(
                    uiState = defaultUiState,
                    isAuthenticated = true,
                    selectedDestination = NavBarDestination.MyRoadmap,
                    onDestinationSelected = {},
                    onSearchQueryChange = {},
                    onClearSearchClick = {},
                    onFilterSelected = {},
                    onRoadmapClick = { roadmapClickedId = it },
                    onRoadmapCtaClick = { ctaClickedId = it },
                    onRetryClick = {},
                    onCreateWithAiClick = {},
                    onExploreRoadmapsClick = {}
                )
            }
        }

        // Compact Card title should be clickable
        composeRule.onNodeWithText("Backend Intern Roadmap").performClick()
        assertEquals("roadmap-1", roadmapClickedId)

        // CTA Button should trigger cta callback
        val ctaLabel = targetContext.getString(R.string.my_roadmap_cta_continue)
        composeRule.onNodeWithText(ctaLabel).performClick()
        assertEquals("roadmap-1", ctaClickedId)
    }
}
