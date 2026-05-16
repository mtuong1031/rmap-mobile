package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.SkillCard
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralDisabledColor
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
private fun SectionHeader(
    title: String,
    badgeText: String,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Text(
            text = badgeText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.3.sp,
                color = badgeColor
            ),
            modifier = Modifier
                .padding(horizontal = Dimens.spacingMicro, vertical = Dimens.spacingXsPlus)
        )
    }
}

@Composable
fun CuratedPathsSection(
    roadmapItems: List<BookmarkRoadmapCardUiModel>,
    savedCount: Int,
    onActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        SectionHeader(
            title = stringResource(R.string.bookmarks_section_curated_paths),
            badgeText = stringResource(R.string.bookmarks_saved_count, savedCount)
        )

        roadmapItems.forEach { item ->
            BookmarkRoadmapCard(
                item = item,
                onActionClick = onActionClick?.let { callback ->
                    { callback(item) }
                },
                onShareClick = onShareClick?.let { callback ->
                    { callback(item) }
                },
                onBookmarkClick = {}
            )
        }
    }
}

@Composable
fun SpecificSkillsSection(
    skillItems: List<SkillCardUiModel>,
    pinsCount: Int,
    onSkillClick: ((SkillCardUiModel) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        SectionHeader(
            title = stringResource(R.string.bookmarks_section_specific_skills),
            badgeText = stringResource(R.string.bookmarks_pins_count, pinsCount)
        )

        skillItems.forEach { item ->
            SkillCard(
                item = item,
                onClick = onSkillClick?.let { callback ->
                    { callback(item) }
                }
            )
        }
    }
}

@Composable
fun FooterHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.7f)
            .padding(bottom = Dimens.spacingHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = NeutralDisabledColor,
            modifier = Modifier.size(Dimens.controlSm)
        )

        Text(
            text = stringResource(R.string.bookmarks_footer_hint),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                color = NeutralDisabledColor,
                textAlign = TextAlign.Center
            )
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun FooterHintPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        FooterHint(modifier = Modifier.padding(Dimens.spacingLg))
    }
}
