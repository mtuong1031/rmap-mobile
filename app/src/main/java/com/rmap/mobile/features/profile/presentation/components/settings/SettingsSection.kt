package com.rmap.mobile.features.profile.presentation.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileSettingAction

@Composable
fun SettingsSection(
    onItemClick: (ProfileSettingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Text(
            text = stringResource(id = R.string.profile_account_settings_title),
            modifier = Modifier.padding(start = Dimens.spacingSm),
            style = AppTextStyles.sectionEyebrow.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        )

        RMapCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.card,
            border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)),
            shadowElevation = Dimens.cardElevationSm,
            shadowColor = Color(0x10298CF7)
        ) {
            Column(modifier = Modifier.padding(Dimens.spacingSm)) {
                SettingsItem(
                    icon = Icons.Outlined.PersonOutline,
                    title = stringResource(id = R.string.profile_setting_personal_info),
                    isDestructive = false,
                    onClick = { onItemClick(ProfileSettingAction.PersonalInfo) }
                )
                SettingsItem(
                    icon = Icons.Outlined.NotificationsNone,
                    title = stringResource(id = R.string.profile_setting_notifications),
                    subtitle = stringResource(id = R.string.profile_setting_notifications_subtitle),
                    isDestructive = false,
                    onClick = { onItemClick(ProfileSettingAction.Notifications) }
                )
                SettingsItem(
                    icon = Icons.Outlined.Shield,
                    title = stringResource(id = R.string.profile_setting_privacy),
                    isDestructive = false,
                    onClick = { onItemClick(ProfileSettingAction.Privacy) }
                )
                SettingsItem(
                    icon = Icons.Outlined.Link,
                    title = stringResource(id = R.string.profile_setting_connected_accounts),
                    isDestructive = false,
                    showArrow = true,
                    showDivider = false,
                    onClick = { onItemClick(ProfileSettingAction.ConnectedAccounts) }
                )
            }
        }

        SignOutButton(onClick = { onItemClick(ProfileSettingAction.SignOut) })
    }
}

@Composable
private fun SignOutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .clickable(onClick = onClick),
        shape = AppShapes.button,
        border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
        shadowElevation = Dimens.cardElevationSm,
        shadowColor = Color(0x14EF4444)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = Dimens.spacingXl),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.iconXxl)
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(Dimens.iconSm)
                )
            }

            Text(
                text = stringResource(id = R.string.profile_setting_sign_out),
                modifier = Modifier.padding(start = Dimens.spacingMd),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun SettingsSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        SettingsSection(onItemClick = {})
    }
}


