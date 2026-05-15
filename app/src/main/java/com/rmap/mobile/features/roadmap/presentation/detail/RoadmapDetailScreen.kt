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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            title = "HTML & CSS",
            status = ModuleStatus.COMPLETED,
            progressPercent = 100,
            icon = Icons.Outlined.Code,
            subLessons = listOf(
                SubLessonUiModel("Semantic HTML", ModuleStatus.COMPLETED),
                SubLessonUiModel("CSS Flexbox & Grid", ModuleStatus.COMPLETED),
                SubLessonUiModel("Responsive Design", ModuleStatus.COMPLETED)
            )
        ),
        ModuleCardUiModel(
            title = "JavaScript Basics",
            status = ModuleStatus.IN_PROGRESS,
            progressPercent = 45,
            icon = Icons.Outlined.DataObject,
            subLessons = listOf(
                SubLessonUiModel("ES6+ Syntax", ModuleStatus.COMPLETED),
                SubLessonUiModel("Asynchronous JS", ModuleStatus.IN_PROGRESS),
                SubLessonUiModel("DOM Manipulation", ModuleStatus.LOCKED)
            )
        )
    )

    val frameworkEcosystem = listOf(
        ModuleCardUiModel(
            title = "React Fundamentals",
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
                    start = 16.dp,
                    end = 16.dp,
                    top = 48.dp, // Space for top bar
                    bottom = innerPadding.calculateBottomPadding() + 48.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
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
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionHeader(
                            title = "Core Web Fundamentals",
                            dotColor = MaterialTheme.colorScheme.primary
                        )
                        coreWebFundamentals.forEach { module ->
                            ModuleCard(item = module)
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionHeader(
                            title = "Framework Ecosystem",
                            dotColor = NeutralDisabledColor
                        )
                        frameworkEcosystem.forEach { module ->
                            ModuleCard(item = module)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AiScholarTipCard(
                        currentModule = "Asynchronous JS",
                        recommendedTopic = "Promises",
                        nextModule = "DOM Manipulation"
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 16.dp, height = 6.dp)
                .background(color = dotColor, shape = CircleShape)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
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
