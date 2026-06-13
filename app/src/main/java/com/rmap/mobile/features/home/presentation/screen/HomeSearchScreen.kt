package com.rmap.mobile.features.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchAiSuggestionSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchAiSuggestionUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomePopularSearchesSection
import com.rmap.mobile.features.home.presentation.components.search.HomeRecentSearchesSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchHeader
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapsSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapsSkeletonSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemDefaults
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillsSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillsSkeletonSection
import kotlinx.coroutines.delay

private const val SearchAutoFocusDelayMillis = 100L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeSearchScreen(
    query: String,
    recentSearches: List<String>,
    popularSearches: List<String>,
    roadmaps: List<HomeSearchRoadmapItemUiModel>,
    skills: List<HomeSearchSkillItemUiModel>,
    roadmapTotal: Int,
    skillTotal: Int,
    aiSuggestion: HomeSearchAiSuggestionUiModel?,
    isLoading: Boolean,
    hasMoreRoadmaps: Boolean,
    hasMoreSkills: Boolean,
    isLoadingMoreRoadmaps: Boolean,
    isLoadingMoreSkills: Boolean,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearRecentSearchesClick: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onRemoveRecentSearchClick: (String) -> Unit,
    onPopularSearchClick: (String) -> Unit,
    onRoadmapClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    onSeeMoreRoadmapsClick: () -> Unit,
    onSkillClick: (HomeSearchSkillItemUiModel) -> Unit,
    onSeeMoreSkillsClick: () -> Unit,
    onCreateWithAiClick: (HomeSearchAiSuggestionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val showSearchResults = query.isNotBlank()
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(SearchAutoFocusDelayMillis)
        searchFocusRequester.requestFocus()
        keyboardController?.show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = Dimens.spacingScreenTopCompact,
            bottom = Dimens.spacingScreenBottomCompact + Dimens.spacingMassive
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingHuge)
    ) {
        item {
            HomeSearchHeader(
                modifier = Modifier.padding(horizontal = Dimens.spacingLg),
                query = query,
                placeholder = stringResource(R.string.home_search_screen_placeholder),
                onQueryChange = onQueryChange,
                onBackClick = onBackClick,
                focusRequester = searchFocusRequester
            )
        }

        if (showSearchResults) {
            if (isLoading) {
                item {
                    HomeSearchRoadmapsSkeletonSection(
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                    )
                }
                item {
                    HomeSearchSkillsSkeletonSection(
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                    )
                }
            } else {
                if (roadmaps.isNotEmpty()) {
                    item {
                        HomeSearchRoadmapsSection(
                            title = stringResource(R.string.home_search_roadmaps_title),
                            roadmaps = roadmaps,
                            metadataSeparatorText = stringResource(R.string.separator_bullet),
                            seeMoreText = stringResource(R.string.action_see_more),
                            resultCountText = stringResource(
                                R.string.home_search_roadmaps_found_count,
                                roadmapTotal
                            ),
                            canSeeMore = hasMoreRoadmaps,
                            isLoadingMore = isLoadingMoreRoadmaps,
                            onRoadmapClick = onRoadmapClick,
                            onSeeMoreClick = onSeeMoreRoadmapsClick,
                            modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                        )
                    }
                }

                if (skills.isNotEmpty()) {
                    item {
                        HomeSearchSkillsSection(
                            title = stringResource(R.string.home_search_skills_title),
                            skills = skills,
                            seeMoreText = stringResource(R.string.action_see_more),
                            resultCountText = stringResource(
                                R.string.home_search_skills_found_count,
                                skillTotal
                            ),
                            canSeeMore = hasMoreSkills,
                            isLoadingMore = isLoadingMoreSkills,
                            onSkillClick = onSkillClick,
                            onSeeMoreClick = onSeeMoreSkillsClick,
                            modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                        )
                    }
                }

                if (aiSuggestion != null) {
                    item {
                        HomeSearchAiSuggestionSection(
                            title = stringResource(R.string.home_search_ai_suggestion_title),
                            suggestion = aiSuggestion,
                            onCreateWithAiClick = onCreateWithAiClick,
                            modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                        )
                    }
                }
            }
        } else {
            if (recentSearches.isNotEmpty()) {
                item {
                    HomeRecentSearchesSection(
                        title = stringResource(R.string.home_search_recent_title),
                        clearAllText = stringResource(R.string.home_search_clear_all),
                        searches = recentSearches,
                        onClearAllClick = onClearRecentSearchesClick,
                        onSearchClick = onRecentSearchClick,
                        onRemoveSearchClick = onRemoveRecentSearchClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                    )
                }
            }

            item {
                HomePopularSearchesSection(
                    title = stringResource(R.string.home_search_popular_title),
                    searches = popularSearches,
                    onSearchClick = onPopularSearchClick,
                    modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                )
            }

        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun HomeSearchScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        HomeSearchScreen(
            query = "React",
            recentSearches = listOf(
                "React roadmap",
                "CSS Grid",
                "Backend developer",
                "DevOps",
                "AI Engineer"
            ),
            popularSearches = listOf(
                "Frontend",
                "React",
                "Backend",
                "DevOps",
                "Data Analyst",
                "AI Engineer"
            ),
            roadmaps = listOf(
                HomeSearchRoadmapItemUiModel(
                    id = "react-fundamentals",
                    title = "React Fundamentals",
                    categoryLabel = "Web Development",
                    metadataText = "4 weeks",
                    leadingIcon = Icons.Outlined.TrackChanges,
                    style = HomeSearchRoadmapItemDefaults.reactStyle()
                ),
                HomeSearchRoadmapItemUiModel(
                    id = "frontend-starter",
                    title = "Frontend Interview Prep",
                    categoryLabel = "Web Development",
                    metadataText = "3 weeks",
                    leadingText = "FI",
                    style = HomeSearchRoadmapItemDefaults.starterStyle()
                )
            ),
            skills = listOf(
                HomeSearchSkillItemUiModel(
                    id = "frontend-react",
                    title = "Frontend",
                    parentText = "Part of: React Fundamentals"
                ),
                HomeSearchSkillItemUiModel(
                    id = "frontend-pro",
                    title = "Frontend",
                    parentText = "Part of: Frontend Pro"
                )
            ),
            roadmapTotal = 2,
            skillTotal = 2,
            aiSuggestion = HomeSearchAiSuggestionUiModel(
                id = "react-roadmap",
                title = "Create a personalized React roadmap",
                description = "Generate a roadmap based on your goal, current skills, and timeline.",
                actionText = "Create with AI"
            ),
            isLoading = false,
            hasMoreRoadmaps = true,
            hasMoreSkills = true,
            isLoadingMoreRoadmaps = false,
            isLoadingMoreSkills = false,
            onQueryChange = {},
            onBackClick = {},
            onClearRecentSearchesClick = {},
            onRecentSearchClick = {},
            onRemoveRecentSearchClick = {},
            onPopularSearchClick = {},
            onRoadmapClick = {},
            onSeeMoreRoadmapsClick = {},
            onSkillClick = {},
            onSeeMoreSkillsClick = {},
            onCreateWithAiClick = {}
        )
    }
}
