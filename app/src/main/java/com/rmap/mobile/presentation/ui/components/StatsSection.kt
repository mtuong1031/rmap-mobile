package com.rmap.mobile.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R

@Composable
fun StatsSection(
    streak: Int,
    certificates: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
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
