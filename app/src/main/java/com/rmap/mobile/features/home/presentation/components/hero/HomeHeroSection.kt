package com.rmap.mobile.features.home.presentation.components.hero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun HomeHeroSection(
    modifier: Modifier = Modifier,
    sectionHorizontalPadding: Dp = Dimens.spacingScreenHorizontal,
    sectionTitle: String,
    continueText: String,
    nextUnlockPrefix: String,
    learningPlans: List<HomeLearningPlanUiModel>,
    onContinueClick: (HomeLearningPlanUiModel) -> Unit,
    onCreateRoadmapWithAiClick: () -> Unit = {},
    onExploreReadyMadeClick: () -> Unit = {},
) {
    LearningPlanSection(
        modifier = modifier.fillMaxWidth(),
        sectionTitle = sectionTitle,
        learningPlans = learningPlans,
        continueText = continueText,
        nextUnlockPrefix = nextUnlockPrefix,
        sectionHorizontalPadding = sectionHorizontalPadding,
        onContinueClick = onContinueClick,
        onCreateRoadmapWithAiClick = onCreateRoadmapWithAiClick,
        onExploreReadyMadeClick = onExploreReadyMadeClick
    )
}

@Composable
private fun LearningPlanSection(
    sectionTitle: String,
    learningPlans: List<HomeLearningPlanUiModel>,
    continueText: String,
    nextUnlockPrefix: String,
    sectionHorizontalPadding: Dp,
    onContinueClick: (HomeLearningPlanUiModel) -> Unit,
    onCreateRoadmapWithAiClick: () -> Unit,
    onExploreReadyMadeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inProgressRoadmaps = getInProgressRoadmaps(learningPlans)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        LearningPlanHeader(
            title = sectionTitle,
            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
        )

        when {
            inProgressRoadmaps.isEmpty() -> {
                HomeHeroEmptyRoadmapCard(
                    onCreateRoadmapWithAiClick = onCreateRoadmapWithAiClick,
                    onExploreReadyMadeClick = onExploreReadyMadeClick,
                    modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                )
            }

            inProgressRoadmaps.size == 1 -> {
                LearningPlanCard(
                    modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                    roadmap = inProgressRoadmaps.first(),
                    variant = LearningPlanCardVariant.Large,
                    continueText = continueText,
                    nextUnlockPrefix = nextUnlockPrefix,
                    onContinueClick = onContinueClick
                )
            }

            else -> {
                LearningPlanCarousel(
                    roadmaps = inProgressRoadmaps,
                    continueText = continueText,
                    nextUnlockPrefix = nextUnlockPrefix,
                    sectionHorizontalPadding = sectionHorizontalPadding,
                    onContinueClick = onContinueClick
                )
            }
        }
    }
}

@Composable
private fun LearningPlanHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    RMapSectionTitle(
        text = title,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeHeroSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeHeroSection(
                sectionTitle = "Today's Learning Plan",
                continueText = "Continue",
                nextUnlockPrefix = "Next unlock: ",
                learningPlans = listOf(
                    HomeLearningPlanUiModel(
                        id = "frontend-pro",
                        roadmapTitle = "Frontend Pro",
                        skillTitle = "Asynchronous JS",
                        chapterText = "Chapter 1/6",
                        requiredSkillText = "Required Skill",
                        timeLeftText = "25 min left",
                        completedRequiredNodes = 6,
                        totalRequiredNodes = 8,
                        nextUnlockText = "DOM Manipulation",
                        lastStudiedAtMillis = 3_000L
                    )
                ),
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeHeroSectionCarouselPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeHeroSection(
                sectionTitle = "Today's Learning Plan",
                continueText = "Continue",
                nextUnlockPrefix = "Next unlock: ",
                learningPlans = listOf(
                    HomeLearningPlanUiModel(
                        id = "frontend-pro",
                        roadmapTitle = "Frontend Pro",
                        skillTitle = "Asynchronous Ronaldo Dos",
                        chapterText = "Chapter 1/6",
                        requiredSkillText = "Required Skill",
                        timeLeftText = "25 min left",
                        completedRequiredNodes = 6,
                        totalRequiredNodes = 8,
                        nextUnlockText = "DOM Manipulation",
                        lastStudiedAtMillis = 5_000L
                    ),
                    HomeLearningPlanUiModel(
                        id = "react-fundamentals",
                        roadmapTitle = "React Fundamentals",
                        skillTitle = "Hooks and State Management",
                        chapterText = "Chapter 2/5",
                        requiredSkillText = "Required Skill",
                        timeLeftText = "18 min left",
                        completedRequiredNodes = 9,
                        totalRequiredNodes = 16,
                        nextUnlockText = "React Query",
                        lastStudiedAtMillis = 4_000L
                    )
                ),
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeHeroSectionEmptyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeHeroSection(
                sectionTitle = "Today's Learning Plan",
                continueText = "",
                nextUnlockPrefix = "",
                learningPlans = emptyList(),
                onContinueClick = {}
            )
        }
    }
}
