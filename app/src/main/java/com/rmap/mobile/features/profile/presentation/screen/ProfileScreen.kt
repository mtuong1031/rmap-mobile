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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.domain.model.AppLanguage
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.presentation.components.achievement.ProfileActivityDayUiModel
import com.rmap.mobile.features.profile.presentation.components.achievement.WeeklyActivityCard
import com.rmap.mobile.features.profile.presentation.components.header.ProfileCard
import com.rmap.mobile.features.profile.presentation.components.header.ProfileHeader
import com.rmap.mobile.features.profile.presentation.components.loading.ProfileContentSkeleton
import com.rmap.mobile.features.profile.presentation.components.settings.LanguageBottomSheet
import com.rmap.mobile.features.profile.presentation.components.settings.SettingsSection
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileSettingAction
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileUiState
import com.rmap.mobile.navigation.NavBarDestination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onSettingClick: (ProfileSettingAction) -> Unit,
    onDestinationSelected: (NavBarDestination) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    onDismissLanguageSheet: () -> Unit = {},
    reselectEvent: Flow<NavBarDestination> = emptyFlow(),
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.More
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(reselectEvent) {
        reselectEvent.collectLatest {
            listState.animateScrollToItem(0)
        }
    }
    
    val weeklyActivityDays = uiState.recentActivity.toProfileActivityDays()
    val activeDaysThisWeek = weeklyActivityDays.count { it.isComplete }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingLg,
                    end = Dimens.spacingLg,
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingXxxl + Dimens.floatingNavBarHeight
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                item {
                    ProfileHeader(
                        title = stringResource(id = R.string.profile_header_title)
                    )
                }

                if (uiState.isLoading) {
                    item {
                        ProfileContentSkeleton()
                    }
                } else {
                    item {
                        ProfileCard(
                            avatarUrl = uiState.avatarUrl,
                            name = uiState.name,
                            role = uiState.role,
                            onEditClick = onEditProfile
                        )
                    }

                    item {
                        WeeklyActivityCard(
                            title = stringResource(id = R.string.profile_weekly_activity_title),
                            subtitle = stringResource(id = R.string.profile_weekly_activity_subtitle_count, activeDaysThisWeek),
                            bestLabel = stringResource(id = R.string.profile_weekly_activity_best_count, uiState.longestStreak),
                            days = weeklyActivityDays
                        )
                    }

                    item {
                        SettingsSection(
                            onItemClick = onSettingClick,
                            currentLanguageDisplayName = uiState.currentLanguage.displayName
                        )
                    }
                }
            }

            if (uiState.showLanguageSheet) {
                LanguageBottomSheet(
                    currentLanguage = uiState.currentLanguage,
                    onLanguageSelected = onLanguageSelected,
                    onDismiss = onDismissLanguageSheet
                )
            }
        }
    }
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

private const val DAYS_PER_WEEK = 7
private const val DATE_ONLY_PATTERN = "yyyy-MM-dd"
private const val UTC_TIME_ZONE = "UTC"

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
                certificates = 2,
                isLoading = false
            ),
            onEditProfile = {},
            onSettingClick = {},
            onDestinationSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenLoadingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                isLoading = true
            ),
            onEditProfile = {},
            onSettingClick = {},
            onDestinationSelected = {}
        )
    }
}
