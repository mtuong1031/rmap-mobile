package com.rmap.mobile.features.home.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.features.home.data.model.HomeActiveRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeDashboardSearchResponseDto
import com.rmap.mobile.features.home.data.model.HomeMetricsDto
import com.rmap.mobile.features.home.data.model.HomeNextUnlockDto
import com.rmap.mobile.features.home.data.model.HomePaceWarningDto
import com.rmap.mobile.features.home.data.model.HomePlanNodeDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapChapterDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapGroupDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapProgressDto
import com.rmap.mobile.features.home.data.model.HomeSearchMetaDto
import com.rmap.mobile.features.home.data.model.HomeSearchPageMetaDto
import com.rmap.mobile.features.home.data.model.HomeSearchRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeSearchRoadmapsPageDto
import com.rmap.mobile.features.home.data.model.HomeSearchSkillDto
import com.rmap.mobile.features.home.data.model.HomeSearchSkillsPageDto

import com.rmap.mobile.features.home.data.model.HomeTemplateRecommendationsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeTemplateTrendingsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTrendingRoadmapDto
import com.rmap.mobile.features.home.data.remote.HomeApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class HomeRepositoryImplTest {
    @Test
    fun `get home content maps all endpoint responses`() = runTest {
        val repository = HomeRepositoryImpl(FakeHomeApi())

        val result = repository.getHomeContent()

        assertTrue(result.isSuccess)
        val content = result.getOrThrow()
        assertEquals("roadmap-1", content.activeRoadmaps.single().roadmapId)
        assertEquals(12.5, content.metrics.roadmapCompletionPct, 0.0)
        assertEquals("template-1", content.recommendations.single().roadmapId)
        assertEquals("trend-1", content.trendings.single().roadmapId)
    }

    @Test
    fun `get home content tolerates nullable optional fields`() = runTest {
        val api = FakeHomeApi().apply {
            recommendationsResponse = Response.success(
                recommendationsDto(
                    roadmap = templateRoadmapDto(
                        description = null,
                        estimatedWeeks = null,
                        durationLabel = null
                    )
                )
            )
            trendingsResponse = Response.success(
                trendingsDto(
                    trending = trendingDto(
                        estimatedWeeks = null,
                        durationLabel = null
                    )
                )
            )
        }
        val repository = HomeRepositoryImpl(api)

        val result = repository.getHomeContent()

        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrThrow().recommendations.single().description)
        assertEquals(null, result.getOrThrow().trendings.single().durationLabel)
    }

    @Test
    fun `get home content returns failure when an endpoint fails`() = runTest {
        val api = FakeHomeApi().apply {
            dashboardResponse = Response.error(
                500,
                """{"code":50000,"message":"Internal error"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = HomeRepositoryImpl(api)

        val result = repository.getHomeContent()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
    }

    @Test
    fun `search dashboard maps roadmap and skill results`() = runTest {
        val repository = HomeRepositoryImpl(FakeHomeApi())

        val result = repository.searchDashboard(query = "full", roadmapPage = 1, skillPage = 1)

        assertTrue(result.isSuccess)
        val search = result.getOrThrow()
        assertEquals("full", search.query)
        assertEquals("roadmap-search-1", search.roadmaps.data.single().roadmapId)
        assertEquals("skill-search-1", search.skills.data.single().skillId)
        assertEquals(true, search.roadmaps.meta.hasNextPage)
        assertEquals(false, search.skills.meta.hasNextPage)
    }

    @Test
    fun `search dashboard tolerates skill result without category fields`() = runTest {
        val api = FakeHomeApi().apply {
            searchResponse = Response.success(
                searchDto(
                    skill = HomeSearchSkillDto(
                        skillId = "skill-search-1",
                        name = "FULL OUTER JOIN",
                        description = "Join data",
                        roleCategory = null,
                        categoryLabel = null,
                        defaultEstimatedHours = null
                    )
                )
            )
        }
        val repository = HomeRepositoryImpl(api)

        val result = repository.searchDashboard(query = "full", roadmapPage = 1, skillPage = 1)

        assertTrue(result.isSuccess)
        val skill = result.getOrThrow().skills.data.single()
        assertEquals("FULL_OUTER_JOIN", skill.roleCategory)
        assertEquals("FULL OUTER JOIN", skill.categoryLabel)
    }

    @Test
    fun `search dashboard returns failure when API fails`() = runTest {
        val api = FakeHomeApi().apply {
            searchResponse = Response.error(
                500,
                """{"code":50000,"message":"Internal error"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = HomeRepositoryImpl(api)

        val result = repository.searchDashboard(query = "full", roadmapPage = 1, skillPage = 1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
    }
}

private class FakeHomeApi : HomeApi {
    var dashboardResponse: Response<HomeDashboardResponseDto> = Response.success(dashboardDto())
    var recommendationsResponse: Response<HomeTemplateRecommendationsResponseDto> =
        Response.success(recommendationsDto())
    var trendingsResponse: Response<HomeTemplateTrendingsResponseDto> = Response.success(trendingsDto())
    var searchResponse: Response<HomeDashboardSearchResponseDto> = Response.success(searchDto())

    override suspend fun getDashboardHome(): Response<HomeDashboardResponseDto> = dashboardResponse

    override suspend fun searchDashboard(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ): Response<HomeDashboardSearchResponseDto> = searchResponse

    override suspend fun getTemplateRecommendations(): Response<HomeTemplateRecommendationsResponseDto> =
        recommendationsResponse



    override suspend fun getTemplateTrendings(): Response<HomeTemplateTrendingsResponseDto> = trendingsResponse
}

private fun dashboardDto(): HomeDashboardResponseDto = HomeDashboardResponseDto(
    activeRoadmaps = listOf(
        HomeActiveRoadmapDto(
            roadmapId = "roadmap-1",
            title = "Backend Roadmap",
            goalName = "Backend",
            isTemplate = false,
            roleCategory = "WEB_DEVELOPMENT",
            startedAt = "2026-06-01T03:22:18.055Z",
            currentGroup = HomeRoadmapGroupDto("group-1", "Internet"),
            planNode = HomePlanNodeDto("node-1", "HTTP", null, "REQUIRED", 3),
            chapter = HomeRoadmapChapterDto(1, 14, "Chapter 1/14"),
            progress = HomeRoadmapProgressDto(1, 49, 2.0),
            nextUnlock = HomeNextUnlockDto("unlock-1", "Frontend Core"),
            paceWarning = HomePaceWarningDto(
                isBehind = true,
                paceDeficitPct = 33.3,
                estimatedDelayDays = 1,
                message = "Finish 1 skill node today.",
                title = "You are behind.",
                actionLabel = "Adjust plan"
            )
        )
    ),
    metrics = HomeMetricsDto(
        roadmapCompletionPct = 12.5,
        streakDays = 2,
        readinessPct = 44.0
    )
)

private fun recommendationsDto(
    roadmap: HomeTemplateRoadmapDto = templateRoadmapDto()
): HomeTemplateRecommendationsResponseDto = HomeTemplateRecommendationsResponseDto(
    roleCategories = emptyList(),
    total = 1,
    relevantRoadmaps = listOf(roadmap)
)

private fun templateRoadmapDto(
    description: String? = "Description",
    estimatedWeeks: Int? = 4,
    durationLabel: String? = "4 weeks"
): HomeTemplateRoadmapDto = HomeTemplateRoadmapDto(
    roadmapId = "template-1",
    title = "GraphQL Roadmap",
    description = description,
    goalName = "GraphQL",
    roleCategory = "WEB_DEVELOPMENT",
    categoryLabel = "Web Development",
    estimatedWeeks = estimatedWeeks,
    durationLabel = durationLabel,
    nodesTotal = 63,
    requiredNodesTotal = 50
)



private fun trendingsDto(
    trending: HomeTrendingRoadmapDto = trendingDto()
): HomeTemplateTrendingsResponseDto = HomeTemplateTrendingsResponseDto(
    total = 1,
    trendings = listOf(trending)
)

private fun trendingDto(
    estimatedWeeks: Int? = 2,
    durationLabel: String? = "2 weeks"
): HomeTrendingRoadmapDto = HomeTrendingRoadmapDto(
    rank = 1,
    roadmapId = "trend-1",
    title = "Kotlin",
    roleCategory = "LANGUAGES_AND_PLATFORMS",
    categoryLabel = "Languages And Platforms",
    estimatedWeeks = estimatedWeeks,
    durationLabel = durationLabel,
    nodesTotal = 10,
    trendText = "Trending now"
)

private fun searchDto(
    skill: HomeSearchSkillDto = HomeSearchSkillDto(
        skillId = "skill-search-1",
        name = "FULL OUTER JOIN",
        description = "Join data",
        roleCategory = "LANGUAGES_AND_PLATFORMS",
        categoryLabel = "Languages And Platforms",
        defaultEstimatedHours = null
    )
): HomeDashboardSearchResponseDto = HomeDashboardSearchResponseDto(
    query = "full",
    roadmaps = HomeSearchRoadmapsPageDto(
        data = listOf(
            HomeSearchRoadmapDto(
                roadmapId = "roadmap-search-1",
                title = "Full Stack Developer Roadmap",
                description = "Learn Full Stack",
                goalName = "Full Stack Developer Roadmap",
                isTemplate = true,
                roadmapType = "template",
                roleCategory = "WEB_DEVELOPMENT",
                categoryLabel = "Web Development",
                estimatedWeeks = null,
                durationLabel = null
            )
        ),
        meta = HomeSearchPageMetaDto(
            page = 1,
            perPage = 5,
            total = 8,
            totalPages = 2
        )
    ),
    skills = HomeSearchSkillsPageDto(
        data = listOf(skill),
        meta = HomeSearchPageMetaDto(
            page = 1,
            perPage = 10,
            total = 1,
            totalPages = 1
        )
    ),
    meta = HomeSearchMetaDto(
        totalResults = 9,
        roadmapPageSize = 5,
        skillPageSize = 10
    )
)
