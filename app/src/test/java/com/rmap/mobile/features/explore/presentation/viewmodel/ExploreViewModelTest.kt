package com.rmap.mobile.features.explore.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `search filters loaded templates locally by title only`() = runTest {
        val repository = FakeRoadmapRepository()
        val viewModel = ExploreViewModel(
            roadmapRepository = repository,
            authRepository = FakeAuthRepository()
        )

        advanceUntilIdle()
        assertEquals(1, repository.searchCalls)
        assertEquals(listOf("Vue Developer Roadmap", "UX Design Roadmap"), viewModel.uiState.value.libraryRoadmaps.map { it.title })
        assertEquals(11, viewModel.uiState.value.categories.first { it.id == "FRAMEWORKS" }.roadmapCount)
        assertEquals("Web", viewModel.uiState.value.categories.first { it.id == "WEB_DEVELOPMENT" }.name)
        assertEquals(9, viewModel.uiState.value.categories.size)

        viewModel.onSearchQueryChange("vue")
        advanceUntilIdle()

        assertEquals(1, repository.searchCalls)
        assertEquals(listOf("Vue Developer Roadmap"), viewModel.uiState.value.libraryRoadmaps.map { it.title })

        viewModel.onSearchQueryChange("frameworks")
        advanceUntilIdle()

        assertEquals(1, repository.searchCalls)
        assertEquals(emptyList<String>(), viewModel.uiState.value.libraryRoadmaps.map { it.title })
    }

    private class FakeRoadmapRepository : RoadmapRepository {
        var searchCalls: Int = 0
            private set

        override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
            return Result.success(
                listOf(
                    RoadmapCategory(
                        id = "WEB_DEVELOPMENT",
                        name = "Web Development",
                        icon = LearningTopicIcon.Code,
                        shortName = "Web",
                        roadmapCount = 9
                    ),
                    RoadmapCategory(
                        id = "FRAMEWORKS",
                        name = "Frameworks",
                        icon = LearningTopicIcon.Code,
                        shortName = "Frameworks",
                        roadmapCount = 11
                    ),
                    RoadmapCategory(
                        id = "ABSOLUTE_BEGINNERS",
                        name = "Absolute Beginners",
                        icon = LearningTopicIcon.Code,
                        shortName = "Beginner",
                        roadmapCount = 4
                    ),
                    RoadmapCategory(
                        id = "LANGUAGES_AND_PLATFORMS",
                        name = "Languages And Platforms",
                        icon = LearningTopicIcon.Terminal,
                        shortName = "Languages",
                        roadmapCount = 17
                    ),
                    RoadmapCategory(
                        id = "DEVOPS",
                        name = "Devops",
                        icon = LearningTopicIcon.Terminal,
                        shortName = "DevOps",
                        roadmapCount = 7
                    ),
                    RoadmapCategory(
                        id = "DATABASES",
                        name = "Databases",
                        icon = LearningTopicIcon.Storage,
                        shortName = "Databases",
                        roadmapCount = 3
                    ),
                    RoadmapCategory(
                        id = "COMPUTER_SCIENCE",
                        name = "Computer Science",
                        icon = LearningTopicIcon.Science,
                        shortName = "CS",
                        roadmapCount = 11
                    ),
                    RoadmapCategory(
                        id = "DESIGN",
                        name = "Design",
                        icon = LearningTopicIcon.Palette,
                        shortName = "Design",
                        roadmapCount = 2
                    ),
                    RoadmapCategory(
                        id = "CYBER_SECURITY",
                        name = "Cyber Security",
                        icon = LearningTopicIcon.Security,
                        shortName = "Security",
                        roadmapCount = 1
                    )
                )
            )
        }

        override suspend fun searchRoadmaps(
            query: String,
            categoryId: String?,
            page: Int,
            perPage: Int
        ): Result<Pair<List<RoadmapSummary>, Int>> {
            searchCalls += 1
            return Result.success(
                Pair(
                    listOf(
                        roadmap("vue", "Vue Developer Roadmap", "FRAMEWORKS"),
                        roadmap("ux", "UX Design Roadmap", "DESIGN")
                    ),
                    2
                )
            )
        }

        override suspend fun getLearningProgress(): Result<LearningProgress> = error("Not used")

        override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> = error("Not used")

        override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> = error("Not used")

        override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> = error("Not used")

        override suspend fun getLearningNode(roadmapId: String, nodeId: String): Result<LearningNodeDetail> {
            error("Not used")
        }

        override suspend fun getNodeQuiz(roadmapId: String, nodeId: String): Result<NodeQuiz> = error("Not used")

        override suspend fun submitNodeQuiz(
            roadmapId: String,
            nodeId: String,
            answers: List<NodeQuizAnswer>
        ): Result<NodeQuizSubmissionResult> = error("Not used")

        override suspend fun getMilestoneDetail(roadmapId: String, milestoneId: String): Result<MilestoneDetail> = error("Not used")
        override suspend fun submitMilestone(roadmapId: String, milestoneId: String, repoUrl: String): Result<MilestoneSubmission> = error("Not used")
        override suspend fun getRoadmapNodeLearningContent(roadmapId: String, nodeId: String, skillId: String): Result<SkillLearningContent> = error("Not used")
        override suspend fun startRoadmap(roadmapId: String): Result<Unit> = error("Not used")
        override suspend fun updateNodeProgress(roadmapId: String, nodeId: String, status: LearningStatus): Result<NodeProgressUpdateResult> = error("Not used")


        private fun roadmap(id: String, title: String, categoryId: String): RoadmapSummary {
            return RoadmapSummary(
                id = id,
                title = title,
                totalLessonsCount = 0,
                completedLessonsCount = 0,
                difficulty = LearningDifficulty.Intermediate,
                durationLabel = "Self-paced",
                icon = LearningTopicIcon.Code,
                categoryId = categoryId
            )
        }
    }

    private class FakeAuthRepository : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)

        override suspend fun login(email: String, password: String): Result<User> = error("Not used")

        override suspend fun register(email: String, password: String, fullName: String): Result<User> = error("Not used")

        override suspend fun logout(): Result<Unit> = error("Not used")

        override suspend fun getCurrentUser(): Result<User> = error("Not used")
    }
}
