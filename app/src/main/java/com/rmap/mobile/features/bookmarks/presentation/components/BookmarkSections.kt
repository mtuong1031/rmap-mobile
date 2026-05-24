package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

fun LazyListScope.bookmarkRoadmapList(
    roadmapItems: List<BookmarkRoadmapCardUiModel>,
    savedCount: Int,
    onActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?
) {
    item(key = "bookmark-roadmap-header") {
        BookmarkSectionHeader(
            title = stringResource(R.string.bookmarks_section_curated_paths),
            badgeText = stringResource(R.string.bookmarks_saved_count, savedCount)
        )
    }

    if (roadmapItems.isEmpty()) {
        item(key = "bookmark-roadmap-empty") {
            EmptyBookmarkState()
        }
    } else {
        items(
            items = roadmapItems,
            key = { item -> "bookmark-roadmap-${item.id}" }
        ) { item ->
            SavedRoadmapCard(
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

fun LazyListScope.bookmarkSkillList(
    skillItems: List<SkillCardUiModel>,
    pinsCount: Int,
    onSkillClick: ((SkillCardUiModel) -> Unit)?
) {
    item(key = "bookmark-skill-header") {
        BookmarkSectionHeader(
            title = stringResource(R.string.bookmarks_section_specific_skills),
            badgeText = stringResource(R.string.bookmarks_pins_count, pinsCount)
        )
    }

    if (skillItems.isEmpty()) {
        item(key = "bookmark-skill-empty") {
            EmptyBookmarkState()
        }
    } else {
        items(
            items = skillItems,
            key = { item -> "bookmark-skill-${item.title}-${item.parentPathName}" }
        ) { item ->
            SavedRoadmapSkill(
                item = item,
                actionLabel = stringResource(R.string.home_hero_continue),
                onClick = onSkillClick?.let { callback ->
                    { callback(item) }
                },
                onActionClick = onSkillClick?.let { callback ->
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
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.controlSm)
        )

        Text(
            text = stringResource(R.string.bookmarks_footer_hint),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
