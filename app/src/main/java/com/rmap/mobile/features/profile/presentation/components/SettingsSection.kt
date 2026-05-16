package com.rmap.mobile.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

enum class SettingType {
    PERSONAL_INFO,
    NOTIFICATIONS,
    LEARNING_GOALS,
    PRIVACY,
    SIGN_OUT
}

@Composable
fun SettingsSection(
    onItemClick: (SettingType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Text(
            text = stringResource(id = R.string.profile_account_settings_title),
            modifier = Modifier.padding(start = Dimens.spacingSm),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .appCardShadow(shape = AppCardDefaults.shape),
            shape = AppCardDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
            border = AppCardDefaults.border()
        ) {
            Column(modifier = Modifier.padding(vertical = Dimens.spacingSm)) {
                SettingsItem(
                    icon = Icons.Outlined.PersonOutline,
                    title = stringResource(id = R.string.profile_setting_personal_info),
                    isDestructive = false,
                    onClick = { onItemClick(SettingType.PERSONAL_INFO) }
                )
                SettingsItem(
                    icon = Icons.Outlined.NotificationsNone,
                    title = stringResource(id = R.string.profile_setting_notifications),
                    isDestructive = false,
                    onClick = { onItemClick(SettingType.NOTIFICATIONS) }
                )
                SettingsItem(
                    icon = Icons.Outlined.TrackChanges,
                    title = stringResource(id = R.string.profile_setting_learning_goals),
                    isDestructive = false,
                    onClick = { onItemClick(SettingType.LEARNING_GOALS) }
                )
                SettingsItem(
                    icon = Icons.Outlined.Policy,
                    title = stringResource(id = R.string.profile_setting_privacy),
                    isDestructive = false,
                    onClick = { onItemClick(SettingType.PRIVACY) }
                )
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    title = stringResource(id = R.string.profile_setting_sign_out),
                    isDestructive = true,
                    showArrow = false,
                    onClick = { onItemClick(SettingType.SIGN_OUT) }
                )
            }
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
