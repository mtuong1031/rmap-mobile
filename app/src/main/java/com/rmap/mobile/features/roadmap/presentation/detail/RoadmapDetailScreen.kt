package com.rmap.mobile.features.roadmap.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.navigation.NavBarDestination
import com.rmap.mobile.core.ui.components.AppNavigationBar
import com.rmap.mobile.features.roadmap.presentation.detail.components.AiScholarTipCard
import com.rmap.mobile.features.roadmap.presentation.detail.components.ModuleCard
import com.rmap.mobile.features.roadmap.presentation.detail.components.ModuleCardUiModel
import com.rmap.mobile.features.roadmap.presentation.detail.components.ModuleStatus
import com.rmap.mobile.features.roadmap.presentation.detail.components.RoadmapHeroCard
import com.rmap.mobile.features.roadmap.presentation.detail.components.SubLessonUiModel
import com.rmap.mobile.core.ui.components.BackgroundDecorator
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralDisabledColor
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun RoadmapDetailScreen(
    uiState: RoadmapDetailUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Bookmarks,
    onDestinationSelected: (NavBarDestination) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)
    val interactionSource = remember { MutableInteractionSource() }

    // Dummy data from Figma
    val coreWebFundamentals = listOf(
        ModuleCardUiModel(
            title = stringResource(R.string.roadmap_detail_module_html_css),
            status = ModuleStatus.COMPLETED,
            progressPercent = 100,
            icon = Icons.Outlined.Code,
            subLessons = listOf(
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_semantic_html),
                    ModuleStatus.COMPLETED
                ),
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_css_flexbox_grid),
                    ModuleStatus.COMPLETED
                ),
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_responsive_design),
                    ModuleStatus.COMPLETED
                )
            )
        ),
        ModuleCardUiModel(
            title = stringResource(R.string.roadmap_detail_module_javascript_basics),
            status = ModuleStatus.IN_PROGRESS,
            progressPercent = 45,
            icon = Icons.Outlined.DataObject,
            subLessons = listOf(
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_es6_syntax),
                    ModuleStatus.COMPLETED
                ),
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_async_js),
                    ModuleStatus.IN_PROGRESS
                ),
                SubLessonUiModel(
                    stringResource(R.string.roadmap_detail_lesson_dom_manipulation),
                    ModuleStatus.LOCKED
                )
            )
        )
    )

    val frameworkEcosystem = listOf(
        ModuleCardUiModel(
            title = stringResource(R.string.roadmap_detail_module_react_fundamentals),
            status = ModuleStatus.LOCKED,
            icon = Icons.Outlined.Storage,
            subLessons = emptyList()
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundDecorator(
                scrollOffsetY = scrollY,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingScreenHorizontal,
                    end = Dimens.spacingScreenHorizontal,
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
            ) {
                item {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(Dimens.controlSm)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onBackClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(Dimens.iconLg)
                        )
                    }
                }

                item {
                    RoadmapHeroCard(
                        title = uiState.title,
                        progressFraction = uiState.progressFraction,
                        completedLessons = uiState.completedLessons,
                        totalLessons = uiState.totalLessons
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
                        SectionHeader(
                            title = stringResource(R.string.roadmap_detail_section_core_web_fundamentals),
                            dotColor = MaterialTheme.colorScheme.primary
                        )
                        coreWebFundamentals.forEach { module ->
                            ModuleCard(item = module)
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
                        SectionHeader(
                            title = stringResource(R.string.roadmap_detail_section_framework_ecosystem),
                            dotColor = NeutralDisabledColor
                        )
                        frameworkEcosystem.forEach { module ->
                            ModuleCard(item = module)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Dimens.spacingSm))
                    AiScholarTipCard(
                        currentModule = stringResource(R.string.roadmap_detail_lesson_async_js),
                        recommendedTopic = stringResource(R.string.roadmap_detail_topic_promises),
                        nextModule = stringResource(R.string.roadmap_detail_lesson_dom_manipulation)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = Dimens.spacingLg, height = Dimens.spacingXsPlus)
                .background(color = dotColor, shape = CircleShape)
        )

        Text(
            text = title,
            style = AppTextStyles.sectionTitle.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 1250)
@Composable
private fun RoadmapDetailScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedDestination by remember { mutableStateOf(NavBarDestination.Bookmarks) }
        RoadmapDetailScreen(
            uiState = RoadmapDetailUiState(),
            onBackClick = {},
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it }
        )
    }
}
