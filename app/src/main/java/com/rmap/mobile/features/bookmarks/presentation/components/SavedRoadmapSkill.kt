package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.SkillCard
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.components.SkillStatus
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun SavedRoadmapSkill(
    item: SkillCardUiModel,
    actionLabel: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(modifier = modifier.fillMaxWidth()) {
        SkillCard(
            item = item,
            modifier = Modifier.then(clickModifier),
            onClick = onClick
        )

        SavedSkillBookmarkButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.spacingXl, end = Dimens.spacingXl)
        )

        RMapButton(
            text = actionLabel,
            onClick = { onActionClick?.invoke() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimens.spacingXl, bottom = Dimens.spacingXl)
                .widthIn(min = 102.dp),
            variant = RMapButtonVariant.Primary,
            size = RMapButtonSize.XSmall
        )
    }
}

@Composable
private fun SavedSkillBookmarkButton(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val actionModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(Dimens.controlMd)
            .clip(AppShapes.pill)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .then(actionModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Bookmark,
            contentDescription = stringResource(R.string.content_description_bookmark),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconMd)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun SavedRoadmapSkillPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        SavedRoadmapSkill(
            item = SkillCardUiModel(
                title = "NoSQL Data Modeling",
                parentPathName = "Backend Systems",
                status = SkillStatus.NOT_STARTED,
                statusLabel = "Not Started",
                icon = Icons.Outlined.DataObject
            ),
            actionLabel = "Continue",
            onActionClick = {},
            onBookmarkClick = {}
        )
    }
}
