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
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.domain.model.UserRoadmapProgress
import com.rmap.mobile.features.profile.presentation.components.achievement.AchievementsCard
import com.rmap.mobile.features.profile.presentation.components.achievement.ProfileActivityDayUiModel
import com.rmap.mobile.features.profile.presentation.components.achievement.ProfileAchievementTab
import com.rmap.mobile.features.profile.presentation.components.achievement.ProfileAchievementUiModel
import com.rmap.mobile.features.profile.presentation.components.achievement.WeeklyActivityCard
import com.rmap.mobile.features.profile.presentation.components.achievement.defaultAchievementBrushes
import com.rmap.mobile.features.profile.presentation.components.achievement.defaultAchievementIcons
import com.rmap.mobile.features.profile.presentation.components.header.ProfileCard
import com.rmap.mobile.features.profile.presentation.components.header.ProfileHeader
import com.rmap.mobile.features.profile.presentation.components.progress.ActiveRoadmapsCard
import com.rmap.mobile.features.profile.presentation.components.progress.ProfileRoadmapProgressUiModel
import com.rmap.mobile.features.profile.presentation.components.progress.defaultProfileRoadmapIcons
import com.rmap.mobile.features.profile.presentation.components.settings.SettingsSection
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileSettingAction
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileUiState
import com.rmap.mobile.navigation.NavBarDestination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.max

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
    val activeRoadmapItems = uiState.activeRoadmaps.toProfileRoadmapProgressItems()
    val activeRoadmapCount = activeRoadmapItems.size
    val weeklyActivityDays = uiState.recentActivity.toProfileActivityDays()
    val activeDaysThisWeek = weeklyActivityDays.count { it.isComplete }
    val bestActivityStreak = uiState.recentActivity.longestActivityStreak()

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
                    start = Dimens.spacingLg,
                    end = Dimens.spacingLg,
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
                        subtitle = activeRoadmapSubtitle(activeRoadmapCount),
                        showAllLabel = stringResource(id = R.string.profile_show_all_roadmaps, activeRoadmapCount),
                        collapseLabel = stringResource(id = R.string.profile_show_less_roadmaps),
                        emptyMessage = stringResource(id = R.string.profile_active_roadmaps_empty),
                        items = activeRoadmapItems,
                        isAllRoadmapsVisible = isManagedRoadmapsVisible,
                        onShowAllRoadmapsClick = {
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
                        subtitle = stringResource(id = R.string.profile_weekly_activity_subtitle_count, activeDaysThisWeek),
                        bestLabel = stringResource(id = R.string.profile_weekly_activity_best_count, bestActivityStreak),
                        days = weeklyActivityDays
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
private fun activeRoadmapSubtitle(count: Int): String {
    return if (count == 1) {
        stringResource(id = R.string.profile_active_roadmaps_subtitle_single)
    } else {
        stringResource(id = R.string.profile_active_roadmaps_subtitle_count, count)
    }
}

@Composable
private fun List<UserRoadmapProgress>.toProfileRoadmapProgressItems(): List<ProfileRoadmapProgressUiModel> {
    val icons = defaultProfileRoadmapIcons()
    return map { roadmap ->
        val style = roadmap.roleCategory.toRoadmapStyle()
        ProfileRoadmapProgressUiModel(
            id = roadmap.id,
            title = roadmap.title,
            remainingTime = roadmap.toRoadmapSubtitle(),
            progressPercent = roadmap.completionPercent,
            icon = icons[style.iconIndex],
            accentColor = style.accentColor,
            accentContainerColor = style.accentContainerColor
        )
    }
}

@Composable
private fun UserRoadmapProgress.toRoadmapSubtitle(): String {
    val daysLeft = deadlineDate?.toDaysLeftFromToday()
    if (daysLeft != null) {
        return when {
            daysLeft <= 0 -> stringResource(id = R.string.profile_roadmap_due_today)
            daysLeft >= DAYS_PER_MONTH -> {
                stringResource(id = R.string.profile_roadmap_months_left, daysLeft.toRoundedUnits(DAYS_PER_MONTH))
            }
            daysLeft >= DAYS_PER_WEEK -> {
                stringResource(id = R.string.profile_roadmap_weeks_left, daysLeft.toRoundedUnits(DAYS_PER_WEEK))
            }
            else -> stringResource(id = R.string.profile_roadmap_days_left, daysLeft)
        }
    }

    return estimatedWeeks?.let { weeks ->
        stringResource(id = R.string.profile_roadmap_estimated_weeks, weeks)
    } ?: roleCategory.toProfileRoleLabel()
}

private fun String.toDaysLeftFromToday(): Int? {
    val deadlineMillis = toUtcDateMillisOrNull() ?: return null
    val todayUtc = todayUtcCalendar()

    return TimeUnit.MILLISECONDS.toDays(deadlineMillis - todayUtc.timeInMillis).toInt()
}

private fun String.toUtcDateMillisOrNull(): Long? {
    val parser = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone(UTC_TIME_ZONE)
    }
    return runCatching { parser.parse(this)?.time }.getOrNull()
}

private fun todayUtcCalendar(): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE)).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private fun Int.toRoundedUnits(unitSize: Int): Int {
    return ((this + unitSize - 1) / unitSize).coerceAtLeast(1)
}

@Composable
private fun String.toProfileRoleLabel(): String {
    return trim()
        .replace("_", " ")
        .replace("-", " ")
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }
        .ifBlank { stringResource(id = R.string.profile_roadmap_generic_label) }
}

private fun String.toRoadmapStyle(): RoadmapStyle {
    return when (uppercase()) {
        "WEB_DEVELOPMENT", "FRONTEND", "BACKEND" -> RoadmapStyle(
            iconIndex = 0,
            accentColor = Color(0xFF2B7FFF),
            accentContainerColor = Color(0xFFF0F5FE)
        )
        "MOBILE_DEVELOPMENT", "MOBILE" -> RoadmapStyle(
            iconIndex = 0,
            accentColor = Color(0xFF10B981),
            accentContainerColor = Color(0xFFECFDF5)
        )
        "DATA_SCIENCE", "DATA", "AI" -> RoadmapStyle(
            iconIndex = 1,
            accentColor = Color(0xFF8B5CF6),
            accentContainerColor = Color(0xFFF3F0FF)
        )
        "DEVOPS" -> RoadmapStyle(
            iconIndex = 0,
            accentColor = Color(0xFFFB923C),
            accentContainerColor = Color(0xFFFFF7ED)
        )
        else -> RoadmapStyle(
            iconIndex = 0,
            accentColor = Color(0xFF64748B),
            accentContainerColor = Color(0xFFF1F5F9)
        )
    }
}

private data class RoadmapStyle(
    val iconIndex: Int,
    val accentColor: Color,
    val accentContainerColor: Color
)

@Composable
private fun List<UserDailyActivity>.toProfileActivityDays(): List<ProfileActivityDayUiModel> {
    val activityByDate = associate { activity ->
        activity.activityDate to activity.nodesCompleted
    }

    return currentWeekDateKeys().mapIndexed { index, date ->
        ProfileActivityDayUiModel(
            label = weeklyDayLabel(index),
            isComplete = (activityByDate[date] ?: 0) > 0
        )
    }
}

@Composable
private fun weeklyDayLabel(index: Int): String {
    val resId = when (index) {
        0 -> R.string.profile_day_monday
        1 -> R.string.profile_day_tuesday
        2 -> R.string.profile_day_wednesday
        3 -> R.string.profile_day_thursday
        4 -> R.string.profile_day_friday
        5 -> R.string.profile_day_saturday
        else -> R.string.profile_day_sunday
    }
    return stringResource(id = resId)
}

private fun currentWeekDateKeys(): List<String> {
    val formatter = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.US).apply {
        timeZone = TimeZone.getTimeZone(UTC_TIME_ZONE)
    }
    val weekStart = todayUtcCalendar().apply {
        val dayOfWeek = get(Calendar.DAY_OF_WEEK)
        val mondayOffset = if (dayOfWeek == Calendar.SUNDAY) {
            -6
        } else {
            Calendar.MONDAY - dayOfWeek
        }
        add(Calendar.DATE, mondayOffset)
    }

    return (0 until DAYS_PER_WEEK).map { offset ->
        val day = weekStart.clone() as Calendar
        day.add(Calendar.DATE, offset)
        formatter.format(day.time)
    }
}

private fun List<UserDailyActivity>.longestActivityStreak(): Int {
    val activeDates = mapNotNull { activity ->
        if (activity.nodesCompleted > 0) activity.activityDate.toUtcDateMillisOrNull() else null
    }.distinct().sorted()

    var best = 0
    var current = 0
    var previous: Long? = null
    activeDates.forEach { dateMillis ->
        current = if (previous != null && dateMillis - previous == DAY_MILLIS) {
            current + 1
        } else {
            1
        }
        best = max(best, current)
        previous = dateMillis
    }
    return best
}

private const val DAYS_PER_WEEK = 7
private const val DAYS_PER_MONTH = 30
private const val DATE_ONLY_PATTERN = "yyyy-MM-dd"
private const val UTC_TIME_ZONE = "UTC"
private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)

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

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                name = "Username",
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
