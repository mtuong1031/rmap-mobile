package com.rmap.mobile.features.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRecommendedRoadmapsSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRecommendedRoadmapsSkeletonSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemDefaults
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillStatusDefaults
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillsSection
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSuggestionChipsRow
import kotlinx.coroutines.delay

private const val SearchSkeletonDurationMillis = 3_000L
private const val SearchAutoFocusDelayMillis = 100L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeSearchScreen(
    query: String,
    suggestions: List<String>,
    recentSearches: List<String>,
    popularSearches: List<String>,
    recommendedRoadmaps: List<HomeSearchRoadmapItemUiModel>,
    skills: List<HomeSearchSkillItemUiModel>,
    aiSuggestion: HomeSearchAiSuggestionUiModel?,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onClearRecentSearchesClick: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onRemoveRecentSearchClick: (String) -> Unit,
    onPopularSearchClick: (String) -> Unit,
    onRecommendedRoadmapClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    onRecommendedRoadmapBookmarkClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    onSkillClick: (HomeSearchSkillItemUiModel) -> Unit,
    onCreateWithAiClick: (HomeSearchAiSuggestionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val showSearchResults = query.isNotBlank()
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchLoading by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(SearchAutoFocusDelayMillis)
        searchFocusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(query, showSearchResults) {
        if (showSearchResults) {
            isSearchLoading = true
            delay(SearchSkeletonDurationMillis)
            isSearchLoading = false
        } else {
            isSearchLoading = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = Dimens.spacingScreenTopCompact,
            bottom = Dimens.spacingScreenBottomCompact
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingHuge)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                HomeSearchHeader(
                    modifier = Modifier.padding(horizontal = Dimens.spacingLg),
                    query = query,
                    placeholder = stringResource(R.string.home_search_screen_placeholder),
                    onQueryChange = onQueryChange,
                    onBackClick = onBackClick,
                    onFilterClick = onFilterClick,
                    focusRequester = searchFocusRequester
                )

                HomeSearchSuggestionChipsRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimens.spacingLg),
                    suggestions = suggestions,
                    onSuggestionClick = onSuggestionClick,
                )
            }
        }

        if (showSearchResults) {
            if (isSearchLoading) {
                item {
                    HomeSearchRecommendedRoadmapsSkeletonSection(
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                    )
                }
            } else {
                item {
                    HomeSearchRecommendedRoadmapsSection(
                        title = stringResource(R.string.home_search_roadmaps_title),
                        roadmaps = recommendedRoadmaps,
                        metadataSeparatorText = stringResource(R.string.separator_bullet),
                        onRoadmapClick = onRecommendedRoadmapClick,
                        onBookmarkClick = onRecommendedRoadmapBookmarkClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                    )
                }

                if (skills.isNotEmpty()) {
                    item {
                        HomeSearchSkillsSection(
                            title = stringResource(R.string.home_search_skills_title),
                            skills = skills,
                            onSkillClick = onSkillClick,
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

            item {
                HomePopularSearchesSection(
                    title = stringResource(R.string.home_search_popular_title),
                    searches = popularSearches,
                    onSearchClick = onPopularSearchClick,
                    modifier = Modifier.padding(start = Dimens.spacingLg)
                )
            }

            item {
                HomeSearchRecommendedRoadmapsSection(
                    title = stringResource(R.string.home_search_recommended_title),
                    roadmaps = recommendedRoadmaps,
                    metadataSeparatorText = stringResource(R.string.separator_bullet),
                    onRoadmapClick = onRecommendedRoadmapClick,
                    onBookmarkClick = onRecommendedRoadmapBookmarkClick,
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
            suggestions = listOf("Frontend", "Backend", "React", "AI", "DevOps"),
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
            recommendedRoadmaps = listOf(
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
                    parentText = "Part of: React Fundamentals",
                    statusText = "Not started",
                    statusStyle = HomeSearchSkillStatusDefaults.notStartedStyle()
                ),
                HomeSearchSkillItemUiModel(
                    id = "frontend-pro",
                    title = "Frontend",
                    parentText = "Part of: Frontend Pro",
                    statusText = "In progress",
                    statusStyle = HomeSearchSkillStatusDefaults.inProgressStyle()
                )
            ),
            aiSuggestion = HomeSearchAiSuggestionUiModel(
                id = "react-roadmap",
                title = "Create a personalized React roadmap",
                description = "Generate a roadmap based on your goal, current skills, and timeline.",
                actionText = "Create with AI"
            ),
            onQueryChange = {},
            onBackClick = {},
            onFilterClick = {},
            onSuggestionClick = {},
            onClearRecentSearchesClick = {},
            onRecentSearchClick = {},
            onRemoveRecentSearchClick = {},
            onPopularSearchClick = {},
            onRecommendedRoadmapClick = {},
            onRecommendedRoadmapBookmarkClick = {},
            onSkillClick = {},
            onCreateWithAiClick = {}
        )
    }
}
