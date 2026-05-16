package com.rmap.mobile.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun StatsSection(
    streak: Int,
    certificates: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMdPlus)
    ) {
        StatCard(
            icon = Icons.Outlined.LocalFireDepartment,
            title = stringResource(id = R.string.profile_current_streak_title),
            value = stringResource(id = R.string.profile_streak_value, streak),
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Outlined.WorkspacePremium,
            title = stringResource(id = R.string.profile_certificates_title),
            value = stringResource(id = R.string.profile_certificates_value, certificates),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun StatsSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            StatsSection(streak = 5, certificates = 2)
        }
    }
}
