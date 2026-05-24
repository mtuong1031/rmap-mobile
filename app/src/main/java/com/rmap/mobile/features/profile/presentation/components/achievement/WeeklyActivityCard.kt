package com.rmap.mobile.features.profile.presentation.components.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionCard
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionHeader

@Composable
fun WeeklyActivityCard(
    title: String,
    subtitle: String,
    bestLabel: String,
    days: List<ProfileActivityDayUiModel>,
    modifier: Modifier = Modifier
) {
    ProfileSectionCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ProfileSectionHeader(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
            Text(
                text = bestLabel,
                modifier = Modifier
                    .clip(AppShapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                ActivityDay(day = day)
            }
        }
    }
}

@Composable
private fun ActivityDay(day: ProfileActivityDayUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (day.isComplete) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.isComplete) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            }
        }

        Text(
            text = day.label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (day.isComplete) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold,
            )
        )
    }
}
