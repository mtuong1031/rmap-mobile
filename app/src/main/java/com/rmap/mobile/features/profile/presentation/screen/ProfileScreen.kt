package com.rmap.mobile.features.profile.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.AchievementsCard
import com.rmap.mobile.features.profile.presentation.components.ActiveRoadmapsCard
import com.rmap.mobile.features.profile.presentation.components.ProfileCard
import com.rmap.mobile.features.profile.presentation.components.ProfileHeader
import com.rmap.mobile.features.profile.presentation.components.ProfileActivityDayUiModel
import com.rmap.mobile.features.profile.presentation.components.ProfileAchievementTab
import com.rmap.mobile.features.profile.presentation.components.ProfileAchievementUiModel
import com.rmap.mobile.features.profile.presentation.components.ProfileManagedRoadmapUiModel
import com.rmap.mobile.features.profile.presentation.components.ProfileRoadmapProgressUiModel
import com.rmap.mobile.features.profile.presentation.components.SettingsSection
import com.rmap.mobile.features.profile.presentation.components.WeeklyActivityCard
import com.rmap.mobile.features.profile.presentation.components.defaultAchievementBrushes
import com.rmap.mobile.features.profile.presentation.components.defaultAchievementIcons
import com.rmap.mobile.features.profile.presentation.components.defaultProfileRoadmapIcons
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileSettingAction
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileUiState
import com.rmap.mobile.navigation.NavBarDestination

private const val DEFAULT_VISIBLE_ACHIEVEMENT_COUNT = 2

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onSettingClick: (ProfileSettingAction) -> Unit,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.More
) {
    val listState = rememberLazyListState()
    var isManagedRoadmapsVisible by rememberSaveable { mutableStateOf(false) }
    var selectedAchievementTab by rememberSaveable { mutableStateOf(ProfileAchievementTab.Roadmaps) }
    var areAllAchievementsVisible by rememberSaveable { mutableStateOf(false) }
    val allAchievements = when (selectedAchievementTab) {
        ProfileAchievementTab.Roadmaps -> profileRoadmapAchievementItems()
        ProfileAchievementTab.Skills -> profileSkillAchievementItems()
    }
    val visibleAchievements = if (areAllAchievementsVisible) {
        allAchievements
    } else {
        allAchievements.take(DEFAULT_VISIBLE_ACHIEVEMENT_COUNT)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            RMapNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingXl,
                    end = Dimens.spacingXl,
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingXxxl
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                item {
                    ProfileHeader(
                        title = stringResource(id = R.string.profile_header_title)
                    )
                }

                item {
                    ProfileCard(
                        avatarUrl = uiState.avatarUrl,
                        name = uiState.name,
                        role = uiState.role,
                        onEditClick = onEditProfile
                    )
                }

                item {
                    ActiveRoadmapsCard(
                        title = stringResource(id = R.string.profile_active_roadmaps_title),
                        subtitle = stringResource(id = R.string.profile_active_roadmaps_subtitle),
                        manageLabel = stringResource(id = R.string.profile_manage_roadmaps),
                        collapseLabel = stringResource(id = R.string.profile_show_less_roadmaps),
                        items = profileRoadmapProgressItems(),
                        managedRoadmaps = profileManagedRoadmapItems(),
                        isManagedRoadmapsVisible = isManagedRoadmapsVisible,
                        onManageRoadmapsClick = {
                            isManagedRoadmapsVisible = !isManagedRoadmapsVisible
                        }
                    )
                }

                item {
                    AchievementsCard(
                        title = stringResource(id = R.string.profile_achievements_title),
                        subtitle = stringResource(id = R.string.profile_achievements_subtitle),
                        seeAllLabel = stringResource(
                            id = if (areAllAchievementsVisible) {
                                R.string.profile_show_less
                            } else {
                                R.string.profile_see_all
                            }
                        ),
                        roadmapsTabLabel = stringResource(id = R.string.profile_tab_roadmaps),
                        skillsTabLabel = stringResource(id = R.string.profile_tab_skills),
                        selectedTab = selectedAchievementTab,
                        achievements = visibleAchievements,
                        onTabSelected = { tab ->
                            selectedAchievementTab = tab
                            areAllAchievementsVisible = false
                        },
                        onSeeAllClick = {
                            areAllAchievementsVisible = !areAllAchievementsVisible
                        }
                    )
                }

                item {
                    WeeklyActivityCard(
                        title = stringResource(id = R.string.profile_weekly_activity_title),
                        subtitle = stringResource(id = R.string.profile_weekly_activity_subtitle),
                        bestLabel = stringResource(id = R.string.profile_weekly_activity_best),
                        days = profileActivityDays()
                    )
                }

                item {
                    SettingsSection(onItemClick = onSettingClick)
                }
            }
        }
    }
}

@Composable
private fun profileManagedRoadmapItems(): List<ProfileManagedRoadmapUiModel> {
    val icons = defaultProfileRoadmapIcons()
    return listOf(
        ProfileManagedRoadmapUiModel(
            title = stringResource(id = R.string.profile_manage_fullstack_title),
            description = stringResource(id = R.string.profile_manage_fullstack_description),
            progressPercent = 48,
            icon = icons[0],
            accentColor = Color(0xFF10B981),
            accentContainerColor = Color(0xFFECFDF5)
        ),
        ProfileManagedRoadmapUiModel(
            title = stringResource(id = R.string.profile_manage_mobile_title),
            description = stringResource(id = R.string.profile_manage_mobile_description),
            progressPercent = 18,
            icon = icons[0],
            accentColor = Color(0xFFFB923C),
            accentContainerColor = Color(0xFFFFF7ED)
        ),
        ProfileManagedRoadmapUiModel(
            title = stringResource(id = R.string.profile_manage_data_title),
            description = stringResource(id = R.string.profile_manage_data_description),
            progressPercent = 0,
            icon = icons[1],
            accentColor = Color(0xFF64748B),
            accentContainerColor = Color(0xFFF1F5F9)
        )
    )
}

@Composable
private fun profileRoadmapProgressItems(): List<ProfileRoadmapProgressUiModel> {
    val icons = defaultProfileRoadmapIcons()
    return listOf(
        ProfileRoadmapProgressUiModel(
            title = stringResource(id = R.string.profile_roadmap_frontend_fresher),
            remainingTime = stringResource(id = R.string.profile_roadmap_frontend_time_left),
            progressPercent = 75,
            icon = icons[0],
            accentColor = Color(0xFF2B7FFF),
            accentContainerColor = Color(0xFFF0F5FE)
        ),
        ProfileRoadmapProgressUiModel(
            title = stringResource(id = R.string.profile_roadmap_uiux_master),
            remainingTime = stringResource(id = R.string.profile_roadmap_uiux_time_left),
            progressPercent = 32,
            icon = icons[1],
            accentColor = Color(0xFF8B5CF6),
            accentContainerColor = Color(0xFFF3F0FF)
        )
    )
}

@Composable
private fun profileRoadmapAchievementItems(): List<ProfileAchievementUiModel> {
    val icons = defaultAchievementIcons()
    val brushes = defaultAchievementBrushes()
    return listOf(
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_achievement_frontend_starter),
            status = stringResource(id = R.string.profile_achievement_roadmap_completed),
            completedAt = stringResource(id = R.string.profile_achievement_completed_two_days),
            icon = icons[0],
            brush = brushes[0]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_achievement_web_design_basics),
            status = stringResource(id = R.string.profile_achievement_roadmap_completed),
            completedAt = stringResource(id = R.string.profile_achievement_completed_one_month),
            icon = icons[1],
            brush = brushes[1]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_achievement_mobile_foundations),
            status = stringResource(id = R.string.profile_achievement_roadmap_completed),
            completedAt = stringResource(id = R.string.profile_achievement_completed_two_months),
            icon = icons[0],
            brush = brushes[0]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_achievement_api_integration),
            status = stringResource(id = R.string.profile_achievement_roadmap_completed),
            completedAt = stringResource(id = R.string.profile_achievement_completed_three_months),
            icon = icons[1],
            brush = brushes[1]
        )
    )
}

@Composable
private fun profileSkillAchievementItems(): List<ProfileAchievementUiModel> {
    val icons = defaultAchievementIcons()
    val brushes = defaultAchievementBrushes()
    return listOf(
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_skill_react_components),
            status = stringResource(id = R.string.profile_achievement_skill_mastered),
            completedAt = stringResource(id = R.string.profile_achievement_completed_yesterday),
            icon = icons[1],
            brush = brushes[1]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_skill_css_layouts),
            status = stringResource(id = R.string.profile_achievement_skill_mastered),
            completedAt = stringResource(id = R.string.profile_achievement_completed_one_week),
            icon = icons[0],
            brush = brushes[0]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_skill_accessibility),
            status = stringResource(id = R.string.profile_achievement_skill_mastered),
            completedAt = stringResource(id = R.string.profile_achievement_completed_two_weeks),
            icon = icons[1],
            brush = brushes[1]
        ),
        ProfileAchievementUiModel(
            title = stringResource(id = R.string.profile_skill_prototyping),
            status = stringResource(id = R.string.profile_achievement_skill_mastered),
            completedAt = stringResource(id = R.string.profile_achievement_completed_one_month),
            icon = icons[0],
            brush = brushes[0]
        )
    )
}

@Composable
private fun profileActivityDays(): List<ProfileActivityDayUiModel> {
    return listOf(
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_monday), isComplete = true),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_tuesday), isComplete = true),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_wednesday), isComplete = true),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_thursday), isComplete = true),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_friday), isComplete = false),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_saturday), isComplete = false),
        ProfileActivityDayUiModel(label = stringResource(id = R.string.profile_day_sunday), isComplete = false)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                name = "Thinh Duy",
                role = "Aspiring Frontend Developer",
                avatarUrl = "",
                xp = 450,
                streak = 5,
                certificates = 2
            ),
            onEditProfile = {},
            onSettingClick = {},
            onDestinationSelected = {}
        )
    }
}
