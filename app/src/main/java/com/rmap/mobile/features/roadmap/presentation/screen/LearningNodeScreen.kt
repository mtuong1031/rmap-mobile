package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.viewmodel.LearningNodeStatusUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.LearningNodeUiState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.LearningResourceUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement

@Composable
fun LearningNodeScreen(
    uiState: LearningNodeUiState,
    onBackClick: () -> Unit,
    onTakeQuizClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

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

            uiState.errorMessage != null -> {
                LearningNodeErrorState(
                    message = uiState.errorMessage.ifBlank {
                        stringResource(R.string.roadmap_learning_error_fallback)
                    },
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.spacingScreenHorizontal,
                        top = Dimens.controlXl + Dimens.spacingLg,
                        end = Dimens.spacingScreenHorizontal,
                        bottom = LearningNodeBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
                ) {
                    item {
                        LearningNodeHeaderCard(uiState = uiState)
                    }

                    if (uiState.prerequisites.isNotEmpty()) {
                        item {
                            LearningPrerequisitesSection(prerequisites = uiState.prerequisites)
                        }
                    }

                    item {
                        LearningResourcesSection(
                            resources = uiState.resources,
                            onResourceClick = { resource ->
                                runCatching { uriHandler.openUri(resource.url) }
                            }
                        )
                    }
                }

                LearningNodeBottomAction(
                    isQuizAvailable = uiState.isQuizAvailable,
                    status = uiState.status,
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

        LearningNodeTopBar(
            title = stringResource(R.string.roadmap_learning_title),
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun LearningNodeTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.controlXl)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.spacingLg),
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
            text = title,
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
private fun LearningNodeHeaderCard(
    uiState: LearningNodeUiState,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = uiState.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    uiState.skillName?.let { skillName ->
                        Text(
                            text = skillName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                RoadmapPill(
                    text = stringResource(uiState.status.labelResId()),
                    containerColor = uiState.status.containerColor(),
                    contentColor = uiState.status.contentColor()
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoadmapPill(
                    text = stringResource(uiState.requirement.labelResId()),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
                uiState.estimatedHours?.let { estimatedHours ->
                    RoadmapPill(
                        text = stringResource(R.string.roadmap_learning_estimated_hours, estimatedHours),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = uiState.description
                    ?: uiState.skillDescription
                    ?: stringResource(R.string.roadmap_learning_description_empty),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun LearningPrerequisitesSection(
    prerequisites: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionTitle(text = stringResource(R.string.roadmap_learning_prerequisites_title))
        RoadmapDecoratedCard(
            shape = AppShapes.button,
            containerColor = MaterialTheme.colorScheme.surface,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            shadow = false
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spacingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                prerequisites.forEach { prerequisite ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.iconSm)
                        )
                        Text(
                            text = prerequisite,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningResourcesSection(
    resources: List<LearningResourceUiModel>,
    onResourceClick: (LearningResourceUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionTitle(text = stringResource(R.string.roadmap_learning_resources_title))

        if (resources.isEmpty()) {
            RoadmapDecoratedCard(
                shape = AppShapes.button,
                containerColor = MaterialTheme.colorScheme.surface,
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                shadow = false
            ) {
                Text(
                    text = stringResource(R.string.roadmap_learning_resources_empty),
                    modifier = Modifier.padding(Dimens.spacingLg),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                resources
                    .sortedWith(compareByDescending<LearningResourceUiModel> { it.isPrimary }.thenBy { it.title })
                    .forEach { resource ->
                        LearningResourceRow(
                            resource = resource,
                            onClick = { onResourceClick(resource) }
                        )
                    }
            }
        }
    }
}

@Composable
private fun LearningResourceRow(
    resource: LearningResourceUiModel,
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
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                    Text(
                        text = resource.type.toResourceTypeLabel(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = stringResource(R.string.separator_bullet),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = if (resource.isFree) {
                            stringResource(R.string.roadmap_learning_resource_free)
                        } else {
                            stringResource(R.string.roadmap_learning_resource_paid)
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    if (resource.isPrimary) {
                        Text(
                            text = stringResource(R.string.separator_bullet),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = stringResource(R.string.roadmap_learning_resource_primary),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
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
private fun LearningNodeBottomAction(
    isQuizAvailable: Boolean,
    status: LearningNodeStatusUiModel,
    onTakeQuizClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (status == LearningNodeStatusUiModel.Completed) {
                Text(
                    text = stringResource(R.string.roadmap_learning_completed_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            } else if (status == LearningNodeStatusUiModel.Locked) {
                Text(
                    text = stringResource(R.string.roadmap_learning_locked_quiz_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            } else if (!isQuizAvailable) {
                Text(
                    text = stringResource(R.string.roadmap_learning_quiz_locked_hint),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            RMapButton(
                text = stringResource(
                    if (status == LearningNodeStatusUiModel.Completed) {
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
private fun LearningNodeErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.spacingXxl),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.error
            )
        )
        RMapButton(
            text = stringResource(R.string.action_retry),
            onClick = onRetryClick,
            variant = RMapButtonVariant.Neutral,
            size = RMapButtonSize.Small
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

private fun LearningNodeStatusUiModel.labelResId(): Int {
    return when (this) {
        LearningNodeStatusUiModel.Completed -> R.string.roadmap_detail_status_completed
        LearningNodeStatusUiModel.InProgress -> R.string.roadmap_detail_status_in_progress
        LearningNodeStatusUiModel.NotStarted -> R.string.roadmap_detail_status_not_started
        LearningNodeStatusUiModel.Locked -> R.string.roadmap_detail_locked
    }
}

@Composable
private fun LearningNodeStatusUiModel.containerColor() = when (this) {
    LearningNodeStatusUiModel.Completed -> MaterialTheme.colorScheme.tertiaryContainer
    LearningNodeStatusUiModel.InProgress -> MaterialTheme.colorScheme.inversePrimary
    LearningNodeStatusUiModel.NotStarted -> MaterialTheme.colorScheme.primaryContainer
    LearningNodeStatusUiModel.Locked -> MaterialTheme.colorScheme.surfaceContainerLow
}

@Composable
private fun LearningNodeStatusUiModel.contentColor() = when (this) {
    LearningNodeStatusUiModel.Completed -> MaterialTheme.colorScheme.tertiary
    LearningNodeStatusUiModel.InProgress -> MaterialTheme.colorScheme.primary
    LearningNodeStatusUiModel.NotStarted -> MaterialTheme.colorScheme.primary
    LearningNodeStatusUiModel.Locked -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun RoadmapNodeRequirement.labelResId(): Int {
    return when (this) {
        RoadmapNodeRequirement.Required -> R.string.roadmap_detail_status_required
        RoadmapNodeRequirement.Optional -> R.string.roadmap_detail_status_optional
    }
}

private fun String.toResourceTypeLabel(): String {
    return lowercase()
        .replace("_", " ")
        .replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
}

private val LearningNodeBottomPadding =
    Dimens.controlXl + Dimens.spacingScreenBottomCompact + Dimens.spacingXxl

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun LearningNodeScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        LearningNodeScreen(
            uiState = LearningNodeUiState(
                title = "What is Domain Name?",
                description = "Understand what domain names are, how DNS resolves them, and how browsers reach a web server.",
                skillName = "Web Fundamentals",
                estimatedHours = 2,
                status = LearningNodeStatusUiModel.InProgress,
                resources = listOf(
                    LearningResourceUiModel(
                        id = "1",
                        title = "MDN: What is a domain name?",
                        url = "https://developer.mozilla.org/",
                        type = "ARTICLE",
                        isFree = true,
                        isPrimary = true
                    )
                ),
                prerequisites = listOf("HTML & CSS"),
                isQuizAvailable = true,
                isLoading = false
            ),
            onBackClick = {},
            onTakeQuizClick = {},
            onRetryClick = {}
        )
    }
}
