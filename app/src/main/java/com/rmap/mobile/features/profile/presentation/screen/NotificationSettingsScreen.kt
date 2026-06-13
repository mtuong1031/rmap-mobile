package com.rmap.mobile.features.profile.presentation.screen

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.ProfileSettingsTopBar
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.rmap.mobile.features.profile.presentation.viewmodel.ReminderFrequency

@Composable
fun NotificationSettingsScreen(
    uiState: NotificationSettingsUiState,
    onBackClick: () -> Unit,
    onNotificationPermissionStateChanged: (Boolean) -> Unit,
    onNotificationPermissionDenied: () -> Unit,
    onAllowNotificationsChange: (Boolean) -> Unit,
    onLearningRemindersEnabledChange: (Boolean) -> Unit,
    onStreakProtectionEnabledChange: (Boolean) -> Unit,
    onAiRoadmapUpdatesEnabledChange: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onReminderFrequencySelected: (ReminderFrequency) -> Unit,
    modifier: Modifier = Modifier,
    isDebugNotificationTestVisible: Boolean = false,
    onSendTestNotificationClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    var hasNotificationPermission by remember {
        mutableStateOf(if (isInspectionMode) uiState.isNotificationPermissionGranted else context.hasNotificationPermission())
    }
    var hasExactAlarmPermission by remember {
        mutableStateOf(if (isInspectionMode) true else context.hasExactAlarmPermission())
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted || context.hasNotificationPermission()
        if (hasNotificationPermission) {
            onAllowNotificationsChange(true)
        } else {
            onNotificationPermissionDenied()
        }
    }

    LaunchedEffect(hasNotificationPermission) {
        onNotificationPermissionStateChanged(hasNotificationPermission)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ProfileSettingsTopBar(
                titleResId = R.string.notification_settings_title,
                onBackClick = onBackClick
            )

            NotificationSettingsContent(
                uiState = uiState,
                onAllowNotificationsChange = onAllowNotificationsChange,
                onLearningRemindersEnabledChange = onLearningRemindersEnabledChange,
                onStreakProtectionEnabledChange = onStreakProtectionEnabledChange,
                onAiRoadmapUpdatesEnabledChange = onAiRoadmapUpdatesEnabledChange,
                onRequestNotificationPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        hasNotificationPermission = true
                        onAllowNotificationsChange(true)
                    }
                },
                onReminderTimeSelected = onReminderTimeSelected,
                isExactAlarmPermissionGranted = hasExactAlarmPermission,
                onOpenExactAlarmSettingsClick = {
                    context.openExactAlarmSettings()
                    hasExactAlarmPermission = context.hasExactAlarmPermission()
                },
                modifier = Modifier.padding(
                    start = Dimens.spacingXl,
                    top = Dimens.spacingXxl,
                    end = Dimens.spacingXl
                )
            )
        }
    }
}

@Composable
private fun NotificationSettingsContent(
    uiState: NotificationSettingsUiState,
    onAllowNotificationsChange: (Boolean) -> Unit,
    onLearningRemindersEnabledChange: (Boolean) -> Unit,
    onStreakProtectionEnabledChange: (Boolean) -> Unit,
    onAiRoadmapUpdatesEnabledChange: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    isExactAlarmPermissionGranted: Boolean,
    onOpenExactAlarmSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTimeDialogVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        RMapCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.card,
            border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(Dimens.spacingSm)) {
                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_allow_title),
                    trailingContent = {
                        Switch(
                            checked = uiState.allowNotifications,
                            onCheckedChange = { isAllowed ->
                                if (isAllowed && !uiState.isNotificationPermissionGranted) {
                                    onRequestNotificationPermission()
                                } else {
                                    onAllowNotificationsChange(isAllowed)
                                }
                            },
                            colors = notificationSwitchColors()
                        )
                    }
                )

                if (!uiState.isNotificationPermissionGranted) {
                    NotificationPermissionNotice()
                }

                if (
                    uiState.allowNotifications &&
                    uiState.learningRemindersEnabled &&
                    !isExactAlarmPermissionGranted
                ) {
                    ExactAlarmPermissionNotice(onOpenSettingsClick = onOpenExactAlarmSettingsClick)
                }

                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_learning_reminders_title),
                    subtitle = stringResource(id = R.string.notification_learning_reminders_subtitle),
                    enabled = uiState.allowNotifications,
                    trailingContent = {
                        Switch(
                            checked = uiState.learningRemindersEnabled,
                            enabled = uiState.allowNotifications,
                            onCheckedChange = onLearningRemindersEnabledChange,
                            colors = notificationSwitchColors()
                        )
                    }
                )

                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_streak_protection_title),
                    subtitle = stringResource(id = R.string.notification_streak_protection_subtitle),
                    enabled = uiState.allowNotifications,
                    trailingContent = {
                        Switch(
                            checked = uiState.streakProtectionEnabled,
                            enabled = uiState.allowNotifications,
                            onCheckedChange = onStreakProtectionEnabledChange,
                            colors = notificationSwitchColors()
                        )
                    }
                )

                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_ai_roadmap_updates_title),
                    subtitle = stringResource(id = R.string.notification_ai_roadmap_updates_subtitle),
                    enabled = uiState.allowNotifications,
                    trailingContent = {
                        Switch(
                            checked = uiState.aiRoadmapUpdatesEnabled,
                            enabled = uiState.allowNotifications,
                            onCheckedChange = onAiRoadmapUpdatesEnabledChange,
                            colors = notificationSwitchColors()
                        )
                    }
                )

                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_reminder_time_title),
                    enabled = uiState.areReminderControlsEnabled,
                    onClick = { isTimeDialogVisible = true },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.reminderTime,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(Dimens.iconSm)
                            )
                        }
                    }
                )

            }
        }

    }

    if (isTimeDialogVisible) {
        ReminderTimeDialog(
            selectedTime = uiState.reminderTime,
            onDismiss = { isTimeDialogVisible = false },
            onTimeSelected = { reminderTime ->
                onReminderTimeSelected(reminderTime)
                isTimeDialogVisible = false
            }
        )
    }
}

@Composable
private fun notificationSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.surface,
    checkedTrackColor = MaterialTheme.colorScheme.primary,
    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
    uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
)

private const val HOUR_MAX_VALUE = 23
private const val MINUTE_MAX_VALUE = 59

@Composable
private fun NotificationPermissionNotice() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(Dimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
    ) {
        Text(
            text = stringResource(id = R.string.notification_permission_required_title),
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stringResource(id = R.string.notification_permission_required_body),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun ExactAlarmPermissionNotice(
    onOpenSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(Dimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
    ) {
        Text(
            text = stringResource(id = R.string.notification_exact_alarm_required_title),
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stringResource(id = R.string.notification_exact_alarm_required_body),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        )
        TextButton(onClick = onOpenSettingsClick) {
            Text(text = stringResource(id = R.string.notification_exact_alarm_open_settings))
        }
    }
}

@Composable
private fun NotificationSettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(enabled = enabled, onClick = onClick)
    } else {
        modifier
    }

    Column(
        modifier = rowModifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.55f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (subtitle == null) 56.dp else 78.dp)
                .padding(horizontal = Dimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold)
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfacePlaceholderLight,
                            fontWeight = FontWeight.Medium)
                    )
                }
            }

            trailingContent()
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.spacingLg)
                    .height(Dimens.borderThin)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
private fun ReminderTimeDialog(
    selectedTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val (initialHour, initialMinute) = remember(selectedTime) {
        selectedTime.toHourMinuteOrDefault()
    }
    var selectedHour by remember(selectedTime) { mutableStateOf(initialHour) }
    var selectedMinute by remember(selectedTime) { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.notification_time_dialog_title)) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TimeWheelHeight),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeWheelColumn(
                    selectedValue = initialHour,
                    maxValue = HOUR_MAX_VALUE,
                    selectedSuffix = stringResource(id = R.string.notification_time_hour_suffix),
                    onValueChanged = { selectedHour = it }
                )
                TimeWheelColumn(
                    selectedValue = initialMinute,
                    maxValue = MINUTE_MAX_VALUE,
                    selectedSuffix = stringResource(id = R.string.notification_time_minute_suffix),
                    onValueChanged = { selectedMinute = it }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected("%02d:%02d".format(selectedHour, selectedMinute))
                }
            ) {
                Text(text = stringResource(id = R.string.action_done))
            }
        }
    )
}

@Composable
private fun TimeWheelColumn(
    selectedValue: Int,
    maxValue: Int,
    selectedSuffix: String,
    onValueChanged: (Int) -> Unit
) {
    val itemCount = maxValue + 1
    val initialIndex = remember(selectedValue, itemCount) {
        TimeWheelLoopCenterIndex - (TimeWheelLoopCenterIndex % itemCount) + selectedValue
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()
    val itemHeightPx = with(LocalDensity.current) { TimeWheelItemHeight.toPx() }
    val currentIndex by remember(listState, itemHeightPx) {
        derivedStateOf {
            listState.selectedTimeWheelIndex(itemHeightPx)
        }
    }
    val currentValue = currentIndex.toTimeWheelValue(itemCount)

    LaunchedEffect(listState, itemHeightPx, itemCount) {
        snapshotFlowValue(listState, itemHeightPx, itemCount)
            .distinctUntilChanged()
            .collect(onValueChanged)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(TimeWheelColumnWidth)
            .fillMaxHeight(),
        contentPadding = PaddingValues(vertical = TimeWheelItemHeight * 2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(TimeWheelLoopItemCount) { index ->
            val value = index.toTimeWheelValue(itemCount)
            val isSelected = value == currentValue
            Box(
                modifier = Modifier
                    .height(TimeWheelItemHeight)
                    .fillMaxWidth()
                    .clickable {
                        onValueChanged(value)
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                currentIndex.closestTimeWheelIndex(
                                    targetValue = value,
                                    itemCount = itemCount
                                )
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSelected) {
                        "%02d%s".format(value, selectedSuffix)
                    } else {
                        "%02d".format(value)
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        lineHeight = TimeWheelItemTextLineHeight
                    )
                )
            }
        }
    }
}

private fun snapshotFlowValue(
    listState: LazyListState,
    itemHeightPx: Float,
    itemCount: Int
) = androidx.compose.runtime.snapshotFlow {
    listState.selectedTimeWheelIndex(itemHeightPx).toTimeWheelValue(itemCount)
}

private fun LazyListState.selectedTimeWheelIndex(itemHeightPx: Float): Int {
    return firstVisibleItemIndex + if (firstVisibleItemScrollOffset > itemHeightPx / 2f) 1 else 0
}

private fun Int.toTimeWheelValue(itemCount: Int): Int {
    return ((this % itemCount) + itemCount) % itemCount
}

private fun Int.closestTimeWheelIndex(
    targetValue: Int,
    itemCount: Int
): Int {
    val currentValue = toTimeWheelValue(itemCount)
    var delta = targetValue - currentValue
    if (delta > itemCount / 2) delta -= itemCount
    if (delta < -itemCount / 2) delta += itemCount
    return (this + delta).coerceIn(0, TimeWheelLoopItemCount - 1)
}

private fun String.toHourMinuteOrDefault(): Pair<Int, Int> {
    val parts = split(":")
    if (parts.size != 2) return DEFAULT_REMINDER_HOUR to DEFAULT_REMINDER_MINUTE

    val hour = parts[0].toIntOrNull() ?: return DEFAULT_REMINDER_HOUR to DEFAULT_REMINDER_MINUTE
    val minute = parts[1].toIntOrNull() ?: return DEFAULT_REMINDER_HOUR to DEFAULT_REMINDER_MINUTE
    if (hour !in 0..HOUR_MAX_VALUE || minute !in 0..MINUTE_MAX_VALUE) {
        return DEFAULT_REMINDER_HOUR to DEFAULT_REMINDER_MINUTE
    }

    return hour to minute
}

private val TimeWheelItemHeight = 44.dp
private val TimeWheelHeight = TimeWheelItemHeight * 5
private val TimeWheelColumnWidth = 96.dp
private val TimeWheelItemTextLineHeight = 32.sp
private const val TimeWheelLoopItemCount = 10_000
private const val TimeWheelLoopCenterIndex = TimeWheelLoopItemCount / 2
private const val DEFAULT_REMINDER_HOUR = 20
private const val DEFAULT_REMINDER_MINUTE = 30

private fun Context.hasNotificationPermission(): Boolean {
    val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    return runtimePermissionGranted && NotificationManagerCompat.from(this).areNotificationsEnabled()
}

private fun Context.hasExactAlarmPermission(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val alarmManager = getSystemService(AlarmManager::class.java)
    return alarmManager.canScheduleExactAlarms()
}

private fun Context.openExactAlarmSettings() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

    val intent = Intent(
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        Uri.parse("package:$packageName")
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    runCatching { startActivity(intent) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationSettingsScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        NotificationSettingsScreen(
            uiState = NotificationSettingsUiState(),
            onBackClick = {},
            onNotificationPermissionStateChanged = {},
            onNotificationPermissionDenied = {},
            onAllowNotificationsChange = {},
            onLearningRemindersEnabledChange = {},
            onStreakProtectionEnabledChange = {},
            onAiRoadmapUpdatesEnabledChange = {},
            onReminderTimeSelected = {},
            onReminderFrequencySelected = {}
        )
    }
}
