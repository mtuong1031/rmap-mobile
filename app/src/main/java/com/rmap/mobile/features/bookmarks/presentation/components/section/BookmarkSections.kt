package com.rmap.mobile.features.bookmarks.presentation.components.section

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.SavedRoadmapCard
import com.rmap.mobile.features.bookmarks.presentation.components.skill.SavedRoadmapSkill
import com.rmap.mobile.features.bookmarks.presentation.components.state.EmptyBookmarkState

fun LazyListScope.bookmarkRoadmapList(
    roadmapItems: List<BookmarkRoadmapCardUiModel>,
    onActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?
) {
    item(key = "bookmark-roadmap-header") {
        BookmarkSectionHeader(
            title = stringResource(R.string.bookmarks_section_curated_paths)
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
    onSkillClick: ((SkillCardUiModel) -> Unit)?
) {
    item(key = "bookmark-skill-header") {
        BookmarkSectionHeader(
            title = stringResource(R.string.bookmarks_section_specific_skills)
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
