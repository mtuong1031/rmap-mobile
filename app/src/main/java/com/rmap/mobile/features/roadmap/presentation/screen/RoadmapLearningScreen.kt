package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapLearningUiState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.SkillLearningDetailUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.SkillLearningResourceUiModel

@Composable
fun RoadmapLearningScreen(
    uiState: RoadmapLearningUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit,
    onTakeQuizClick: () -> Unit,
    onMarkCompletedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessageResId != null -> {
                RoadmapLearningMessageState(
                    icon = Icons.Outlined.ErrorOutline,
                    messageResId = uiState.errorMessageResId,
                    actionTextResId = R.string.roadmap_detail_retry,
                    onActionClick = onRetryClick,
                    iconTint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.spacingXxl)
                )
            }

            uiState.skill == null -> {
                RoadmapLearningMessageState(
                    icon = Icons.Outlined.Code,
                    messageResId = R.string.roadmap_learning_empty_description,
                    actionTextResId = R.string.roadmap_detail_retry,
                    onActionClick = onRetryClick,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.spacingXxl)
                )
            }

            else -> {
                RoadmapLearningContent(
                    skill = uiState.skill,
                    resources = uiState.resources,
                    isCompleted = uiState.isCompleted,
                    canTakeQuiz = uiState.canTakeQuiz,
                    canMarkCompleted = uiState.canMarkCompleted,
                    isNodeLocked = uiState.isNodeLocked,
                    isCompleting = uiState.isCompleting,
                    completionBlockedMessageResId = uiState.completionBlockedMessageResId,
                    onResourceClick = onResourceClick,
                    onTakeQuizClick = onTakeQuizClick,
                    onMarkCompletedClick = onMarkCompletedClick
                )
            }
        }

        RoadmapLearningTopBar(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun RoadmapLearningContent(
    skill: SkillLearningDetailUiModel,
    resources: List<SkillLearningResourceUiModel>,
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    canMarkCompleted: Boolean,
    isNodeLocked: Boolean,
    isCompleting: Boolean,
    @StringRes completionBlockedMessageResId: Int?,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit,
    onTakeQuizClick: () -> Unit,
    onMarkCompletedClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.spacingScreenHorizontal,
            top = Dimens.controlXl + Dimens.spacingMd,
            end = Dimens.spacingScreenHorizontal,
            bottom = RoadmapLearningContentBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        item {
            SkillLearningHeaderCard(skill = skill)
        }

        item {
            Text(
                text = stringResource(R.string.roadmap_learning_resources_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (resources.isEmpty()) {
            item {
                RoadmapLearningMessageState(
                    icon = Icons.Outlined.Code,
                    messageResId = R.string.roadmap_learning_resources_empty,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(
                items = resources,
                key = { resource -> resource.id }
            ) { resource ->
                SkillLearningResourceCard(
                    resource = resource,
                    onClick = { onResourceClick(resource) }
                )
            }
        }

        if (!isCompleted) {
            item {
                RMapButton(
                    text = stringResource(R.string.roadmap_learning_take_quiz),
                    onClick = onTakeQuizClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(min = RMapButtonSize.Large.height),
                    variant = RMapButtonVariant.Outline,
                    size = RMapButtonSize.Large,
                    enabled = canTakeQuiz && !isNodeLocked,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(RMapButtonSize.Large.iconSize)
                        )
                    }
                )
            }
        }

        item {
            RMapButton(
                text = stringResource(
                    if (isCompleted) {
                        R.string.roadmap_learning_completed
                    } else {
                        R.string.roadmap_learning_mark_completed
                    }
                ),
                onClick = onMarkCompletedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = RMapButtonSize.Large.height),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                enabled = !isCompleted && canMarkCompleted && !isCompleting,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(RMapButtonSize.Large.iconSize)
                    )
                }
            )
        }

        if (completionBlockedMessageResId != null) {
            item {
                Text(
                    text = stringResource(completionBlockedMessageResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SkillLearningHeaderCard(skill: SkillLearningDetailUiModel) {
    RoadmapDecoratedCard(
        shape = AppShapes.searchBar,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        shadow = true
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = skill.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                RoadmapPill(
                    text = skill.category?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.roadmap_learning_category_general),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = skill.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.roadmap_learning_no_description),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            RoadmapPill(
                text = if (skill.estimatedHours != null && skill.estimatedHours > 0) {
                    stringResource(R.string.roadmap_learning_estimated_hours, skill.estimatedHours)
                } else {
                    stringResource(R.string.roadmap_learning_estimated_hours_unknown)
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SkillLearningResourceCard(
    resource: SkillLearningResourceUiModel,
    onClick: () -> Unit
) {
    RoadmapDecoratedCard(
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        shadow = false
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoadmapPill(
                    text = stringResource(resource.platformLabelResId),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
                RoadmapPill(
                    text = stringResource(
                        if (resource.isFree) {
                            R.string.roadmap_learning_resource_free
                        } else {
                            R.string.roadmap_learning_resource_paid
                        }
                    ),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                resource.levelLabelResId?.let { levelLabelResId ->
                    RoadmapPill(
                        text = stringResource(levelLabelResId),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            RMapButton(
                text = stringResource(R.string.roadmap_learning_open_resource),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Outline,
                size = RMapButtonSize.Medium
            )
        }
    }
}

@Composable
private fun RoadmapLearningTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.spacingScreenHorizontal,
                vertical = Dimens.spacingSm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_description_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(R.string.roadmap_learning_title),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun RoadmapLearningMessageState(
    icon: ImageVector,
    @StringRes messageResId: Int,
    modifier: Modifier = Modifier,
    @StringRes actionTextResId: Int? = null,
    onActionClick: () -> Unit = {},
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier
            .widthIn(max = RoadmapLearningMessageMaxWidth)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(Dimens.iconXxl)
        )
        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionTextResId != null) {
            RMapButton(
                text = stringResource(actionTextResId),
                onClick = onActionClick,
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Medium,
                modifier = Modifier.padding(top = Dimens.spacingSm)
            )
        }
    }
}

private val RoadmapLearningContentBottomPadding =
    Dimens.controlXl + Dimens.spacingScreenBottomCompact + Dimens.spacingXl
private val RoadmapLearningMessageMaxWidth = 320.dp

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapLearningScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapLearningScreen(
            uiState = RoadmapLearningUiState(
                skill = SkillLearningDetailUiModel(
                    id = "skill-rest",
                    name = "REST API Design",
                    description = "Learn how to design clean resource-oriented APIs.",
                    category = "backend",
                    estimatedHours = 8
                ),
                resources = listOf(
                    SkillLearningResourceUiModel(
                        id = "resource-1",
                        title = "HTTP and REST fundamentals",
                        url = "https://example.com",
                        platformLabelResId = R.string.roadmap_learning_platform_youtube,
                        isFree = true,
                        levelLabelResId = R.string.roadmap_learning_level_fresher
                    )
                ),
                isLoading = false
            ),
            onBackClick = {},
            onRetryClick = {},
            onResourceClick = {},
            onTakeQuizClick = {},
            onMarkCompletedClick = {}
        )
    }
}
