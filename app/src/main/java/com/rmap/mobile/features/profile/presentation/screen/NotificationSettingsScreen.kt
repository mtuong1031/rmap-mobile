package com.rmap.mobile.features.profile.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsUiState
import com.rmap.mobile.features.profile.presentation.viewmodel.ReminderFrequency
import com.rmap.mobile.navigation.NavBarDestination

@Composable
fun NotificationSettingsScreen(
    uiState: NotificationSettingsUiState,
    onBackClick: () -> Unit,
    onNotificationPermissionStateChanged: (Boolean) -> Unit,
    onNotificationPermissionDenied: () -> Unit,
    onAllowNotificationsChange: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onReminderFrequencySelected: (ReminderFrequency) -> Unit,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.More,
    isDebugNotificationTestVisible: Boolean = false,
    onSendTestNotificationClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    var hasNotificationPermission by remember {
        mutableStateOf(if (isInspectionMode) uiState.isNotificationPermissionGranted else context.hasNotificationPermission())
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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            RMapNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            NotificationSettingsHeader(onBackClick = onBackClick)

            NotificationSettingsContent(
                uiState = uiState,
                onAllowNotificationsChange = onAllowNotificationsChange,
                onRequestNotificationPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        hasNotificationPermission = true
                        onAllowNotificationsChange(true)
                    }
                },
                onReminderTimeSelected = onReminderTimeSelected,
                onReminderFrequencySelected = onReminderFrequencySelected,
                isDebugNotificationTestVisible = isDebugNotificationTestVisible,
                onSendTestNotificationClick = onSendTestNotificationClick,
                modifier = Modifier.padding(
                    start = Dimens.spacingXl,
                    top = 49.dp,
                    end = Dimens.spacingXl
                )
            )
        }
    }
}

@Composable
private fun NotificationSettingsHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(
                start = Dimens.spacingXl,
                top = Dimens.spacingXxl,
                end = Dimens.spacingXl,
                bottom = Dimens.spacingXlPlus
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.controlSm)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.content_description_back),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }

        Text(
            text = stringResource(id = R.string.notification_settings_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 30.sp
            )
        )
    }
}

@Composable
private fun NotificationSettingsContent(
    uiState: NotificationSettingsUiState,
    onAllowNotificationsChange: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onReminderFrequencySelected: (ReminderFrequency) -> Unit,
    isDebugNotificationTestVisible: Boolean,
    onSendTestNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTimeDialogVisible by remember { mutableStateOf(false) }
    var isFrequencyMenuVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        RMapCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.card,
            border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)),
            shadowElevation = Dimens.cardElevationSm,
            shadowColor = Color(0x10298CF7)
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
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.surface,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                )

                if (!uiState.isNotificationPermissionGranted) {
                    NotificationPermissionNotice()
                }

                NotificationSettingRow(
                    title = stringResource(id = R.string.notification_reminder_time_title),
                    onClick = { isTimeDialogVisible = true },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.reminderTime,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                )
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

                Box {
                    NotificationSettingRow(
                        title = stringResource(id = R.string.notification_reminder_frequency_title),
                        subtitle = stringResource(id = R.string.notification_reminder_frequency_subtitle),
                        showDivider = false,
                        onClick = { isFrequencyMenuVisible = true },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.reminderFrequency.label(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
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

                    DropdownMenu(
                        expanded = isFrequencyMenuVisible,
                        onDismissRequest = { isFrequencyMenuVisible = false }
                    ) {
                        ReminderFrequency.entries.forEach { frequency ->
                            DropdownMenuItem(
                                text = { Text(text = frequency.label()) },
                                onClick = {
                                    onReminderFrequencySelected(frequency)
                                    isFrequencyMenuVisible = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (isDebugNotificationTestVisible) {
            DebugNotificationTestButton(
                isEnabled = uiState.isNotificationPermissionGranted,
                onClick = onSendTestNotificationClick
            )
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
private fun DebugNotificationTestButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.button
    ) {
        Text(
            text = stringResource(id = R.string.notification_debug_send_test),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

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
private fun NotificationSettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Column(modifier = rowModifier.fillMaxWidth()) {
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
                        fontWeight = FontWeight.Bold,
                    )
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfacePlaceholderLight,
                            fontWeight = FontWeight.Medium,
                        )
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
    val reminderTimes = listOf("08:00", "12:30", "20:30", "22:00")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.notification_time_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                reminderTimes.forEach { reminderTime ->
                    Text(
                        text = reminderTime,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(AppShapes.chip)
                            .background(
                                if (reminderTime == selectedTime) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable { onTimeSelected(reminderTime) }
                            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (reminderTime == selectedTime) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_done))
            }
        }
    )
}

@Composable
private fun ReminderFrequency.label(): String {
    return when (this) {
        ReminderFrequency.Daily -> stringResource(id = R.string.notification_frequency_daily)
        ReminderFrequency.Weekly -> stringResource(id = R.string.notification_frequency_weekly)
    }
}

private fun Context.hasNotificationPermission(): Boolean {
    val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    return runtimePermissionGranted && NotificationManagerCompat.from(this).areNotificationsEnabled()
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
            onReminderTimeSelected = {},
            onReminderFrequencySelected = {},
            onDestinationSelected = {}
        )
    }
}

