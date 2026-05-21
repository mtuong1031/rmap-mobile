package com.rmap.mobile.features.profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCard
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsUiState
import com.rmap.mobile.features.profile.presentation.viewmodel.ReminderFrequency
import com.rmap.mobile.navigation.NavBarDestination

@Composable
fun NotificationSettingsScreen(
    uiState: NotificationSettingsUiState,
    onBackClick: () -> Unit,
    onAllowNotificationsChange: (Boolean) -> Unit,
    onReminderTimeSelected: (String) -> Unit,
    onReminderFrequencySelected: (ReminderFrequency) -> Unit,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.More
) {
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
                onReminderTimeSelected = onReminderTimeSelected,
                onReminderFrequencySelected = onReminderFrequencySelected,
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
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant)
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
    onReminderTimeSelected: (String) -> Unit,
    onReminderFrequencySelected: (ReminderFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    var isTimeDialogVisible by remember { mutableStateOf(false) }
    var isFrequencyMenuVisible by remember { mutableStateOf(false) }

    AppCard(
        modifier = modifier.fillMaxWidth(),
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
                        onCheckedChange = onAllowNotificationsChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            )

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
                        fontSize = 15.sp,
                        lineHeight = 23.sp
                    )
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
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

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationSettingsScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        NotificationSettingsScreen(
            uiState = NotificationSettingsUiState(),
            onBackClick = {},
            onAllowNotificationsChange = {},
            onReminderTimeSelected = {},
            onReminderFrequencySelected = {},
            onDestinationSelected = {}
        )
    }
}
