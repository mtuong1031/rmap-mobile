package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
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
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
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
                    isNodeLocked = uiState.isNodeLocked,
                    onResourceClick = onResourceClick,
                    onTakeQuizClick = onTakeQuizClick
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
    isNodeLocked: Boolean,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit,
    onTakeQuizClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.spacingScreenHorizontal,
                top = Dimens.controlXl + Dimens.spacingXl,
                end = Dimens.spacingScreenHorizontal,
                bottom = RoadmapLearningContentBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            item {
                SkillLearningHeaderCard(
                    skill = skill,
                    isCompleted = isCompleted,
                    canTakeQuiz = canTakeQuiz,
                    isNodeLocked = isNodeLocked
                )
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
        }

        RoadmapLearningBottomAction(
            isCompleted = isCompleted,
            canTakeQuiz = canTakeQuiz,
            isNodeLocked = isNodeLocked,
            onTakeQuizClick = onTakeQuizClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = Dimens.spacingScreenHorizontal,
                    end = Dimens.spacingScreenHorizontal,
                    bottom = Dimens.spacingXl
                )
        )
    }
}

@Composable
private fun SkillLearningHeaderCard(
    skill: SkillLearningDetailUiModel,
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
) {
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                RoadmapPill(
                    text = stringResource(
                        roadmapLearningStatusLabelResId(
                            isCompleted = isCompleted,
                            canTakeQuiz = canTakeQuiz,
                            isNodeLocked = isNodeLocked
                        )
                    ),
                    containerColor = roadmapLearningStatusContainerColor(
                        isCompleted = isCompleted,
                        canTakeQuiz = canTakeQuiz,
                        isNodeLocked = isNodeLocked
                    ),
                    contentColor = roadmapLearningStatusContentColor(
                        isCompleted = isCompleted,
                        canTakeQuiz = canTakeQuiz,
                        isNodeLocked = isNodeLocked
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoadmapPill(
                    text = stringResource(R.string.roadmap_detail_status_required),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
                skill.estimatedHours?.takeIf { it > 0 }?.let { estimatedHours ->
                    RoadmapPill(
                        text = stringResource(R.string.roadmap_learning_estimated_hours, estimatedHours),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = skill.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.roadmap_learning_no_description),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
        modifier = Modifier.clickable(onClick = onClick),
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        shadow = false
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.controlMd)
                    .background(MaterialTheme.colorScheme.primaryContainer, AppShapes.iconContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
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

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                    Text(
                        text = stringResource(resource.platformLabelResId),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.separator_bullet),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = stringResource(
                            if (resource.isFree) {
                                R.string.roadmap_learning_resource_free
                            } else {
                                R.string.roadmap_learning_resource_paid
                            }
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

@Composable
private fun RoadmapLearningBottomAction(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean,
    onTakeQuizClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isQuizAvailable = canTakeQuiz && !isNodeLocked
    val isUnavailableQuizAction = !isCompleted && !isQuizAvailable

    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isNodeLocked) {
                Text(
                    text = stringResource(R.string.roadmap_learning_locked_quiz_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }

            RMapButton(
                text = stringResource(
                    if (isCompleted) {
                        R.string.roadmap_learning_completed
                    } else {
                        R.string.roadmap_learning_take_quiz
                    }
                ),
                onClick = onTakeQuizClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                enabled = !isCompleted,
                colors = if (isUnavailableQuizAction) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outline,
                        contentColor = OnSurfacePlaceholderLight
                    )
                } else {
                    null
                }
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

@StringRes
private fun roadmapLearningStatusLabelResId(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
): Int {
    return when {
        isCompleted -> R.string.roadmap_detail_status_completed
        isNodeLocked -> R.string.roadmap_detail_locked
        canTakeQuiz -> R.string.roadmap_detail_status_in_progress
        else -> R.string.roadmap_detail_status_not_started
    }
}

@Composable
private fun roadmapLearningStatusContainerColor(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
): Color {
    return when {
        isCompleted -> MaterialTheme.colorScheme.tertiaryContainer
        isNodeLocked -> MaterialTheme.colorScheme.surfaceContainerLow
        canTakeQuiz -> MaterialTheme.colorScheme.inversePrimary
        else -> MaterialTheme.colorScheme.primaryContainer
    }
}

@Composable
private fun roadmapLearningStatusContentColor(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
): Color {
    return when {
        isCompleted -> MaterialTheme.colorScheme.tertiary
        isNodeLocked -> MaterialTheme.colorScheme.onSurfaceVariant
        canTakeQuiz -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
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
                canTakeQuiz = true,
                isLoading = false
            ),
            onBackClick = {},
            onRetryClick = {},
            onResourceClick = {},
            onTakeQuizClick = {}
        )
    }
}
