package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBorder
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberText
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneSoftBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneDetailStatusUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneSubmissionStatusUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneSubmissionUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneTestCaseUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneTestSuiteUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneSubmissionTestResultUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiState
import com.rmap.mobile.core.ui.components.HighlightStatCardDefaults
import com.rmap.mobile.core.ui.components.HighlightStatCardRow
import com.rmap.mobile.core.ui.components.HighlightStatItemUiModel
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Score
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmber
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberDark
import androidx.compose.material.icons.outlined.AutoAwesome

@Composable
fun RoadmapMilestoneScreen(
    uiState: RoadmapMilestoneUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRepoUrlChanged: (String) -> Unit,
    onTestSuiteToggleClick: () -> Unit,
    onSubmitClick: () -> Unit,
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
                MilestoneErrorState(
                    message = stringResource(uiState.errorMessageResId),
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
                        bottom = MilestoneBottomActionPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
                ) {
                    item {
                        MilestoneHeaderCard(uiState = uiState)
                    }

                    item {
                        MilestoneTestSuiteSection(
                            testSuite = uiState.testSuite,
                            expanded = uiState.isTestSuiteExpanded,
                            onToggleClick = onTestSuiteToggleClick
                        )
                    }

                    uiState.latestSubmission
                        ?.takeIf { it.hasTestExecutionResult }
                        ?.let { submission ->
                        item {
                            MilestoneSubmissionSection(submission = submission)
                        }
                    }
                }

                MilestoneRepositoryCard(
                    repoUrl = uiState.repoUrl,
                    repoUrlErrorResId = uiState.repoUrlErrorResId,
                    enabled = uiState.canSubmit,
                    busy = uiState.isSubmitting,
                    isLocked = uiState.isLocked,
                    onRepoUrlChanged = onRepoUrlChanged,
                    onSubmitClick = onSubmitClick,
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

        MilestoneTopBar(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun MilestoneTopBar(
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
            text = stringResource(R.string.roadmap_milestone_screen_title),
            modifier = Modifier.weight(1f),
            style = AppTextStyles.titleMediumStrong.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MilestoneHeaderCard(
    uiState: RoadmapMilestoneUiState,
    modifier: Modifier = Modifier
) {
    val colors = if (isSystemInDarkTheme()) DarkMilestoneHeaderColors else LightMilestoneHeaderColors

    RoadmapDecoratedCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = colors.border,
        useHeroBackground = true,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                colors.gradientStart,
                colors.gradientEnd
            )
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = colors.decorativeIcon,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.spacingSm)
                .size(Dimens.iconFrameSize + Dimens.spacingMd)
                .rotate(12f)
        )

        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            RoadmapPill(
                text = stringResource(R.string.roadmap_detail_milestone_label),
                containerColor = colors.pillContainer,
                contentColor = colors.accent,
                borderColor = colors.border,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(Dimens.iconXxs)
                    )
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colors.title,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uiState.description?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.roadmap_milestone_project_brief_empty),
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.body)
                )
            }
        }
    }
}

@Composable
private fun MilestoneTestSuiteSection(
    testSuite: RoadmapMilestoneTestSuiteUiModel?,
    expanded: Boolean,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        if (testSuite == null) {
            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                SectionTitle(text = stringResource(R.string.roadmap_milestone_test_suite_title))
                Text(
                    text = stringResource(R.string.roadmap_milestone_test_suite_empty),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                    ) {
                        SectionTitle(text = stringResource(R.string.roadmap_milestone_test_suite_title))
                        Text(
                            text = stringResource(
                                R.string.roadmap_milestone_checks_count,
                                testSuite.testCases.size
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    RoadmapPill(
                        text = stringResource(
                            R.string.roadmap_milestone_pass_threshold,
                            testSuite.passThresholdPercent
                        ),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = testSuite.title,
                    style = AppTextStyles.titleMediumStrong.copy(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    text = testSuite.summary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                        testSuite.testCases.forEach { testCase ->
                            MilestoneTestCaseCard(testCase = testCase)
                        }
                    }
                }
                RMapButton(
                    text = stringResource(
                        if (expanded) {
                            R.string.roadmap_milestone_hide_details
                        } else {
                            R.string.roadmap_milestone_view_details
                        }
                    ),
                    onClick = onToggleClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = RMapButtonVariant.Neutral,
                    size = RMapButtonSize.Small
                )
            }
        }
    }
}

@Composable
private fun MilestoneTestCaseCard(
    testCase: RoadmapMilestoneTestCaseUiModel,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        shadow = false
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Text(
                text = testCase.name,
                style = AppTextStyles.titleMediumStrong.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = testCase.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun MilestoneSubmissionSection(
    submission: RoadmapMilestoneSubmissionUiModel,
    modifier: Modifier = Modifier
) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        RoadmapDecoratedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.card,
            containerColor = MaterialTheme.colorScheme.surface,
            borderColor = MaterialTheme.colorScheme.outlineVariant
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                Text(
                    text = stringResource(submission.status.headingResId()),
                    style = AppTextStyles.titleMediumStrong.copy(
                        color = submission.status.contentColor()
                    )
                )

                val statItems = listOfNotNull(
                    HighlightStatItemUiModel(
                        valueText = submission.attemptNumber.toString(),
                        labelText = stringResource(R.string.roadmap_milestone_attempt),
                        icon = Icons.Outlined.Refresh,
                        style = HighlightStatCardDefaults.neutralStyle()
                    ),
                    submission.passRatePercent?.let {
                        HighlightStatItemUiModel(
                            valueText = "$it%",
                            labelText = stringResource(R.string.roadmap_milestone_score),
                            icon = Icons.Outlined.Score,
                            style = if (it >= 80) HighlightStatCardDefaults.goalStyle() else HighlightStatCardDefaults.streakStyle()
                        )
                    },
                    submission.passedTests?.let {
                        HighlightStatItemUiModel(
                            valueText = "$it/${submission.totalTests}",
                            labelText = stringResource(R.string.roadmap_milestone_tests_passed),
                            icon = Icons.AutoMirrored.Outlined.FactCheck,
                            style = if (it == submission.totalTests) HighlightStatCardDefaults.goalStyle() else HighlightStatCardDefaults.errorStyle()
                        )
                    }
                )

                if (statItems.isNotEmpty()) {
                    HighlightStatCardRow(items = statItems)
                }
            }
        }

        if (submission.testResults.isNotEmpty()) {
            MilestoneSubmissionTestResultsSection(testResults = submission.testResults)
        } else if (!submission.outputLog.isNullOrBlank()) {
            MilestoneSubmissionOutputLogSection(
                outputLog = submission.outputLog,
                clipboardManager = clipboardManager
            )
        }
    }
}

@Composable
private fun MilestoneSubmissionTestResultsSection(
    testResults: List<RoadmapMilestoneSubmissionTestResultUiModel>,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            SectionTitle(text = "Test Results")
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                testResults.forEach { result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = AppShapes.button
                            )
                            .padding(Dimens.spacingLg),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        Icon(
                            imageVector = if (result.passed) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
                            contentDescription = null,
                            tint = if (result.passed) roadmapSuccess else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Dimens.iconMd)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                            Text(
                                text = result.name,
                                style = AppTextStyles.titleMediumStrong.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            if (!result.passed && result.message.isNotBlank()) {
                                Text(
                                    text = result.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneSubmissionOutputLogSection(
    outputLog: String,
    @Suppress("DEPRECATION") clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.roadmap_milestone_raw_output_log),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(outputLog)) },
                    modifier = Modifier.size(Dimens.controlSm)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(R.string.roadmap_milestone_copy_output_log),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.iconMd)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(Dimens.cardRadiusSm)
                    )
                    .padding(Dimens.spacingMd)
            ) {
                Text(
                    text = outputLog,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun MilestoneRepoUrlSection(
    repoUrl: String,
    @androidx.annotation.StringRes errorMessageResId: Int?,
    onRepoUrlChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = stringResource(R.string.roadmap_milestone_repo_url_label),
            style = AppTextStyles.titleMediumStrong.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        RMapTextInput(
            value = repoUrl,
            onValueChange = onRepoUrlChanged,
            placeholder = stringResource(R.string.roadmap_milestone_repo_url_placeholder),
            textStyle = MaterialTheme.typography.bodyLarge,
            showClearButton = true
        )
        errorMessageResId?.let { messageResId ->
            Text(
                text = stringResource(messageResId),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
            )
        }
    }
}

@Composable
private fun MilestoneRepositoryCard(
    repoUrl: String,
    @androidx.annotation.StringRes repoUrlErrorResId: Int?,
    enabled: Boolean,
    busy: Boolean,
    isLocked: Boolean,
    onRepoUrlChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            if (isLocked) {
                SectionTitle(text = stringResource(R.string.roadmap_milestone_repo_url_label))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = AppShapes.button
                        )
                        .padding(Dimens.spacingLg)
                ) {
                    Text(
                        text = stringResource(R.string.roadmap_milestone_locked_hint),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            } else {
                MilestoneRepoUrlSection(
                    repoUrl = repoUrl,
                    errorMessageResId = repoUrlErrorResId,
                    onRepoUrlChanged = onRepoUrlChanged
                )

                RMapButton(
                    text = stringResource(
                        if (busy) {
                            R.string.roadmap_milestone_submitting
                        } else {
                            R.string.roadmap_milestone_submit
                        }
                    ),
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = RMapButtonVariant.Primary,
                    size = RMapButtonSize.Large,
                    enabled = enabled && !busy
                )
            }
        }
    }
}

@Composable
private fun MilestoneErrorState(
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
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
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
        style = AppTextStyles.titleMediumStrong.copy(color = MaterialTheme.colorScheme.onSurface)
    )
}


private fun RoadmapMilestoneSubmissionStatusUiModel.headingResId(): Int {
    return when (this) {
        RoadmapMilestoneSubmissionStatusUiModel.Running -> R.string.roadmap_milestone_submission_running
        RoadmapMilestoneSubmissionStatusUiModel.Passed -> R.string.roadmap_milestone_submission_passed
        RoadmapMilestoneSubmissionStatusUiModel.Failed -> R.string.roadmap_milestone_submission_failed
        RoadmapMilestoneSubmissionStatusUiModel.Error -> R.string.roadmap_milestone_submission_error
        RoadmapMilestoneSubmissionStatusUiModel.Unknown -> R.string.roadmap_milestone_submission_unknown
    }
}

@Composable
private fun RoadmapMilestoneSubmissionStatusUiModel.contentColor() = when (this) {
    RoadmapMilestoneSubmissionStatusUiModel.Passed -> roadmapSuccess
    RoadmapMilestoneSubmissionStatusUiModel.Failed,
    RoadmapMilestoneSubmissionStatusUiModel.Error -> MaterialTheme.colorScheme.error
    RoadmapMilestoneSubmissionStatusUiModel.Running -> if (isSystemInDarkTheme()) {
        DarkMilestoneHeaderColors.accent
    } else {
        roadmapAmberText
    }
    RoadmapMilestoneSubmissionStatusUiModel.Unknown -> MaterialTheme.colorScheme.onSurface
}

private data class MilestoneHeaderColors(
    val gradientStart: Color,
    val gradientEnd: Color,
    val border: Color,
    val pillContainer: Color,
    val accent: Color,
    val title: Color,
    val body: Color,
    val decorativeIcon: Color
)

private val LightMilestoneHeaderColors = MilestoneHeaderColors(
    gradientStart = roadmapMilestoneSoftBg.copy(alpha = 0.96f),
    gradientEnd = roadmapAmberBg.copy(alpha = 0.98f),
    border = roadmapAmberBorder,
    pillContainer = roadmapAmberBg,
    accent = roadmapAmber,
    title = roadmapAmberDark,
    body = roadmapAmberText,
    decorativeIcon = roadmapAmber.copy(alpha = 0.18f)
)

private val DarkMilestoneHeaderColors = MilestoneHeaderColors(
    gradientStart = Color(0xFF35290F),
    gradientEnd = Color(0xFF292314),
    border = Color(0xFF80621E),
    pillContainer = Color(0xFF49370F),
    accent = Color(0xFFFBBF24),
    title = Color(0xFFFDE68A),
    body = Color(0xFFF5C75B),
    decorativeIcon = Color(0xFFFBBF24).copy(alpha = 0.16f)
)

private val MilestoneBottomActionPadding =
    Dimens.controlXl + Dimens.controlXl + Dimens.controlXl + Dimens.spacingScreenBottomCompact

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun RoadmapMilestoneScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapMilestoneScreen(
            uiState = roadmapMilestonePreviewState(),
            onBackClick = {},
            onRetryClick = {},
            onRepoUrlChanged = {},
            onTestSuiteToggleClick = {},
            onSubmitClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390, heightDp = 844)
@Composable
private fun RoadmapMilestoneScreenDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        RoadmapMilestoneScreen(
            uiState = roadmapMilestonePreviewState(),
            onBackClick = {},
            onRetryClick = {},
            onRepoUrlChanged = {},
            onTestSuiteToggleClick = {},
            onSubmitClick = {}
        )
    }
}

private fun roadmapMilestonePreviewState() = RoadmapMilestoneUiState(
    title = "Basic API Server",
    description = "Construct a raw Node.js HTTP server that handles routing and parses JSON bodies.",
    status = RoadmapMilestoneDetailStatusUiModel.InProgress,
    testSuite = RoadmapMilestoneTestSuiteUiModel(
        title = "Raw Node.js API Server Evaluation",
        summary = "This suite verifies a manual HTTP server using Node.js built-in modules.",
        passThresholdPercent = 80,
        testCases = listOf(
            RoadmapMilestoneTestCaseUiModel(
                name = "Dependency Audit",
                description = "Verifies that no high-level frameworks like Express are listed in package.json."
            ),
            RoadmapMilestoneTestCaseUiModel(
                name = "HTTP Module Integration",
                description = "Checks for usage of the native node:http module to create the server."
            )
        )
    ),
    latestSubmission = RoadmapMilestoneSubmissionUiModel(
        repoUrl = "https://github.com/example/rmap-test",
        status = RoadmapMilestoneSubmissionStatusUiModel.Error,
        outputLog = "[error]\nspawn docker ENOENT",
        passRatePercent = null,
        passedTests = null,
        totalTests = null,
        attemptNumber = 6,
        testResults = listOf(
            RoadmapMilestoneSubmissionTestResultUiModel(
                name = "Tailwind CSS Dependency Check",
                message = "Tailwind CSS is missing from package.json",
                passed = false
            ),
            RoadmapMilestoneSubmissionTestResultUiModel(
                name = "Linting and Formatting Setup",
                message = "Requirement met.",
                passed = true
            )
        )
    ),
    repoUrl = "",
    isTestSuiteExpanded = false,
    canSubmit = false,
    isLoading = false
)
