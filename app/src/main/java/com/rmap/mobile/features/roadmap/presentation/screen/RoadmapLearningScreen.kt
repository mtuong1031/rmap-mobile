package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.theme.SuccessContainerLight
import com.rmap.mobile.core.ui.theme.SuccessLight
import com.rmap.mobile.core.utils.parseMarkdownToAnnotatedString
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapLearningUiState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.SkillLearningDetailUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.SkillLearningResourceUiModel
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBorder

@Composable
fun RoadmapLearningScreen(
    uiState: RoadmapLearningUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit,
    onTakeQuizClick: () -> Unit,
    onStartRoadmapForQuizClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isStartForQuizDialogVisible by rememberSaveable { mutableStateOf(false) }
    val requiresRoadmapStartForQuiz = !uiState.isCompleted && !uiState.canTakeQuiz && !uiState.isNodeLocked

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            RoadmapLearningTopBar(
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.errorMessageResId == null && uiState.skill != null) {
                RoadmapLearningBottomAction(
                    isCompleted = uiState.isCompleted,
                    canTakeQuiz = uiState.canTakeQuiz,
                    isNodeLocked = uiState.isNodeLocked,
                    onTakeQuizClick = {
                        if (requiresRoadmapStartForQuiz) {
                            isStartForQuizDialogVisible = true
                        } else {
                            onTakeQuizClick()
                        }
                    },
                    modifier = Modifier.padding(
                        start = Dimens.spacingScreenHorizontal,
                        end = Dimens.spacingScreenHorizontal,
                        bottom = Dimens.spacingXl,
                        top = Dimens.spacingMd
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        nodeTitle = uiState.nodeTitle,
                        requirement = uiState.requirement,
                        skill = uiState.skill,
                        resources = uiState.resources,
                        isCompleted = uiState.isCompleted,
                        canTakeQuiz = uiState.canTakeQuiz,
                        isNodeLocked = uiState.isNodeLocked,
                        onResourceClick = onResourceClick
                    )
                }
            }
        }
    }

    if (isStartForQuizDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isStartingRoadmapForQuiz) {
                    isStartForQuizDialogVisible = false
                }
            },
            title = {
                Text(text = stringResource(R.string.roadmap_learning_start_for_quiz_title))
            },
            text = {
                Text(text = stringResource(R.string.roadmap_learning_start_for_quiz_message))
            },
            confirmButton = {
                TextButton(
                    enabled = !uiState.isStartingRoadmapForQuiz,
                    onClick = {
                        isStartForQuizDialogVisible = false
                        onStartRoadmapForQuizClick()
                    }
                ) {
                    Text(text = stringResource(R.string.roadmap_learning_start_for_quiz_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !uiState.isStartingRoadmapForQuiz,
                    onClick = { isStartForQuizDialogVisible = false }
                ) {
                    Text(text = stringResource(R.string.roadmap_learning_start_for_quiz_cancel))
                }
            }
        )
    }
}

@Composable
private fun RoadmapLearningContent(
    nodeTitle: String,
    requirement: RoadmapNodeRequirement,
    skill: SkillLearningDetailUiModel,
    resources: List<SkillLearningResourceUiModel>,
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.spacingScreenHorizontal,
            top = Dimens.spacingLg,
            end = Dimens.spacingScreenHorizontal,
            bottom = Dimens.spacingXxl
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
    ) {
        item {
            SkillLearningHeaderCard(
                nodeTitle = nodeTitle,
                requirement = requirement,
                skill = skill,
                isCompleted = isCompleted,
                canTakeQuiz = canTakeQuiz,
                isNodeLocked = isNodeLocked
            )
        }
        
        item {
            SkillLearningDetailsSection(
                description = skill.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.roadmap_learning_no_description)
            )
        }

        item {
            SkillLearningResourcesSection(
                resources = resources,
                onResourceClick = onResourceClick
            )
        }
    }
}

@Composable
private fun SkillLearningHeaderCard(
    nodeTitle: String,
    requirement: RoadmapNodeRequirement,
    skill: SkillLearningDetailUiModel,
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        RMapHeroSectionBackground(
            modifier = Modifier.matchParentSize()
        )
        
        Column(
            modifier = Modifier.padding(Dimens.spacingXxl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nodeTitle,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RoadmapPill(
                        text = stringResource(
                            if (requirement == RoadmapNodeRequirement.Required) R.string.roadmap_detail_status_required
                            else R.string.roadmap_detail_status_optional
                        ),
                        containerColor = if (requirement == RoadmapNodeRequirement.Required) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = if (requirement == RoadmapNodeRequirement.Required) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                        borderColor = if (requirement == RoadmapNodeRequirement.Required) MaterialTheme.colorScheme.tertiary.copy(alpha = RequirementBadgeBorderAlpha) else MaterialTheme.colorScheme.outlineVariant
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
                    ),
                    borderColor = roadmapLearningStatusBorderColor(
                        isCompleted = isCompleted,
                        canTakeQuiz = canTakeQuiz,
                        isNodeLocked = isNodeLocked
                    )
                )
            }

            Text(
                text = skill.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            skill.estimatedHours?.takeIf { it > 0 }?.let { estimatedHours ->
                Row(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = AppShapes.chip
                        )
                        .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXs),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                    Text(
                        text = stringResource(R.string.roadmap_learning_estimated_hours, estimatedHours),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillLearningDetailsSection(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionTitle(text = stringResource(R.string.roadmap_learning_about_skill))
        
        RoadmapDecoratedCard(
            shape = AppShapes.button,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            borderColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shadow = false
        ) {
            Row(
                modifier = Modifier.padding(Dimens.spacingLg),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconLg)
                )
                Text(
                    text = parseMarkdownToAnnotatedString(
                        text = description,
                        codeTextColor = MaterialTheme.colorScheme.primary,
                        codeBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
                    )
                )
            }
        }
    }
}

@Composable
private fun SkillLearningResourcesSection(
    resources: List<SkillLearningResourceUiModel>,
    onResourceClick: (SkillLearningResourceUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionTitle(text = stringResource(R.string.roadmap_learning_resources_title))

        if (resources.isEmpty()) {
            RoadmapDecoratedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.button,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                shadow = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.spacingXxl, horizontal = Dimens.spacingLg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(Dimens.iconLg)
                    )
                    Text(
                        text = stringResource(R.string.roadmap_learning_resources_empty),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                resources.forEach { resource ->
                    SkillLearningResourceCard(
                        resource = resource,
                        onClick = { onResourceClick(resource) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillLearningResourceCard(
    resource: SkillLearningResourceUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier.clickable(onClick = onClick),
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
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = AppShapes.iconContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (resource.platformLabelResId) {
                        R.string.roadmap_learning_platform_youtube -> Icons.Outlined.PlayCircle
                        R.string.roadmap_learning_platform_udemy,
                        R.string.roadmap_learning_platform_coursera,
                        R.string.roadmap_learning_platform_course -> Icons.Outlined.School
                        else -> Icons.AutoMirrored.Outlined.Article
                    },
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
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                    Text(
                        text = if (resource.platformLabelResId == R.string.roadmap_learning_platform_other && !resource.rawPlatform.isNullOrBlank()) {
                            resource.rawPlatform.lowercase().replaceFirstChar { it.uppercase() }
                        } else {
                            stringResource(resource.platformLabelResId)
                        },
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
                            color = if (resource.isFree) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                    if (resource.levelLabelResId != null) {
                        Text(
                            text = stringResource(R.string.separator_bullet),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = stringResource(resource.levelLabelResId),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1
                        )
                    }
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

    RoadmapDecoratedCard(
        modifier = modifier.cardShadow(AppShapes.button),
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        borderColor = MaterialTheme.colorScheme.surface,
        shadow = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCompleted) {
                Text(
                    text = stringResource(R.string.roadmap_learning_completed_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            } else if (isNodeLocked) {
                Text(
                    text = stringResource(R.string.roadmap_learning_locked_quiz_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            } else if (!isQuizAvailable) {
                Text(
                    text = stringResource(R.string.roadmap_learning_quiz_locked_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }

            RMapButton(
                text = stringResource(
                    if (isCompleted) {
                        R.string.roadmap_detail_status_completed
                    } else {
                        R.string.roadmap_learning_take_quiz
                    }
                ),
                onClick = onTakeQuizClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Medium,
                enabled = isQuizAvailable
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
            .height(Dimens.controlXl)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(Dimens.controlSm)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_description_back),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.iconLg)
            )
        }
        Text(
            text = stringResource(R.string.roadmap_learning_title),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    )
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
    val isDark = isSystemInDarkTheme()
    return when {
        isDark && isCompleted -> Color(0xFF064E3B)
        isDark && isNodeLocked -> MaterialTheme.colorScheme.surfaceContainerHigh
        isDark && canTakeQuiz -> MaterialTheme.colorScheme.primaryContainer
        else -> when {
            isCompleted -> SuccessContainerLight
            isNodeLocked -> MaterialTheme.colorScheme.surfaceContainerLow
            canTakeQuiz -> MaterialTheme.colorScheme.inversePrimary
            else -> MaterialTheme.colorScheme.primaryContainer
        }
    }
}

@Composable
private fun roadmapLearningStatusContentColor(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
): Color {
    val isDark = isSystemInDarkTheme()
    return when {
        isDark && isCompleted -> Color(0xFF34D399)
        isDark && isNodeLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        isDark && canTakeQuiz -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> when {
            isCompleted -> SuccessLight
            isNodeLocked -> MaterialTheme.colorScheme.onSurfaceVariant
            canTakeQuiz -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.primary
        }
    }
}

@Composable
private fun roadmapLearningStatusBorderColor(
    isCompleted: Boolean,
    canTakeQuiz: Boolean,
    isNodeLocked: Boolean
) = if (isCompleted) roadmapSuccessBorder else null

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

private val RoadmapLearningMessageMaxWidth = 320.dp
private const val RequirementBadgeBorderAlpha = 0.35f

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
                        rawPlatform = "youtube",
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
            onTakeQuizClick = {},
            onStartRoadmapForQuizClick = {}
        )
    }
}
