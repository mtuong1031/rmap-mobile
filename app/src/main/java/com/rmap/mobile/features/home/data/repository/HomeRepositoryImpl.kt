package com.rmap.mobile.features.home.data.repository

import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.database.entity.SyncDataType
import com.rmap.mobile.core.database.sync.SyncManager
import com.rmap.mobile.core.database.sync.SyncVersionDto
import com.rmap.mobile.features.home.data.local.dao.HomeTrendingRoadmapDao
import com.rmap.mobile.features.home.data.local.mapper.toDto
import com.rmap.mobile.features.home.data.local.mapper.toEntity
import com.rmap.mobile.features.home.data.local.mapper.toHomeDto
import com.rmap.mobile.features.home.data.local.mapper.toTemplateCategoryEntity
import com.rmap.mobile.features.home.data.mapper.toHomeContent
import com.rmap.mobile.features.home.data.mapper.toDomain
import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeMetricsDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRecommendationsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateTrendingsResponseDto
import com.rmap.mobile.features.home.data.remote.HomeApi
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.roadmap.data.local.dao.TemplateCategoryDao
import com.rmap.mobile.features.roadmap.data.remote.model.TemplateCategoriesResponseDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HomeRepositoryImpl(
    private val homeApi: HomeApi,
    private val templateCategoryDao: TemplateCategoryDao? = null,
    private val trendingRoadmapDao: HomeTrendingRoadmapDao? = null,
    private val syncManager: SyncManager? = null
) : HomeRepository {
    override suspend fun getHomeContent(): Result<HomeContent> {
        return coroutineScope {
            val serverVersions = syncManager?.getServerVersions()
            val dashboard = async { SafeApiCall.execute { homeApi.getDashboardHome() } }
            val recommendations = async { SafeApiCall.execute { homeApi.getTemplateRecommendations() } }
            val categories = async { getTemplateCategoriesResult(serverVersions) }
            val trendings = async { getTemplateTrendingsResult(serverVersions) }

            val dashboardResult = dashboard.await()
            val recommendationsResult = recommendations.await()
            val trendingsResult = trendings.await()

            val dashboardData = when (dashboardResult) {
                is NetworkResult.Success -> dashboardResult.data
                is NetworkResult.Error -> {
                    if (dashboardResult.code == 401) {
                        HomeDashboardResponseDto(
                            activeRoadmaps = emptyList(),
                            metrics = HomeMetricsDto(0.0, 0, 0.0)
                        )
                    } else {
                        return@coroutineScope Result.failure(dashboardResult.toAppException())
                    }
                }
            }

            val recommendationsData = when (recommendationsResult) {
                is NetworkResult.Success -> recommendationsResult.data
                is NetworkResult.Error -> {
                    if (recommendationsResult.code == 401) {
                        HomeTemplateRecommendationsResponseDto(
                            roleCategories = emptyList(),
                            total = 0,
                            relevantRoadmaps = emptyList()
                        )
                    } else {
                        return@coroutineScope Result.failure(recommendationsResult.toAppException())
                    }
                }
            }

            val trendingsData = when (trendingsResult) {
                is NetworkResult.Success -> trendingsResult.data
                is NetworkResult.Error -> {
                    if (trendingsResult.code == 401) {
                        HomeTemplateTrendingsResponseDto(
                            total = 0,
                            trendings = emptyList()
                        )
                    } else {
                        return@coroutineScope Result.failure(trendingsResult.toAppException())
                    }
                }
            }

            Result.success(
                toHomeContent(
                    dashboard = dashboardData,
                    recommendations = recommendationsData,
                    trendings = trendingsData
                )
            )
        }
    }

    override suspend fun searchDashboard(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ): Result<HomeSearchResult> {
        return when (
            val result = SafeApiCall.execute {
                homeApi.searchDashboard(
                    query = query,
                    roadmapPage = roadmapPage,
                    skillPage = skillPage
                )
            }
        ) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    private suspend fun getTemplateCategoriesResult(
        serverVersions: SyncVersionDto?
    ): NetworkResult<TemplateCategoriesResponseDto> {
        val cachedCategories = templateCategoryDao?.getAll().orEmpty()
        val shouldUseCache = cachedCategories.isNotEmpty() &&
            syncManager?.isStale(SyncDataType.TEMPLATE_CATEGORIES, serverVersions) == false
        if (shouldUseCache) {
            return NetworkResult.Success(cachedCategories.toHomeCategoriesDto(), CACHE_STATUS_CODE)
        }

        return when (val result = SafeApiCall.execute { homeApi.getTemplateCategories() }) {
            is NetworkResult.Success -> {
                templateCategoryDao?.replaceAll(
                    result.data.categories.map { category -> category.toTemplateCategoryEntity() }
                )
                syncManager?.markSynced(SyncDataType.TEMPLATE_CATEGORIES, serverVersions)
                result
            }

            is NetworkResult.Error -> {
                if (cachedCategories.isNotEmpty()) {
                    NetworkResult.Success(cachedCategories.toHomeCategoriesDto(), CACHE_STATUS_CODE)
                } else {
                    result
                }
            }
        }
    }

    private suspend fun getTemplateTrendingsResult(
        serverVersions: SyncVersionDto?
    ): NetworkResult<HomeTemplateTrendingsResponseDto> {
        val cachedTrendings = trendingRoadmapDao?.getAll().orEmpty()
        val shouldUseCache = cachedTrendings.isNotEmpty() &&
            syncManager?.isStale(SyncDataType.TEMPLATE_TRENDINGS, serverVersions) == false
        if (shouldUseCache) {
            return NetworkResult.Success(
                HomeTemplateTrendingsResponseDto(
                    total = cachedTrendings.size,
                    trendings = cachedTrendings.map { roadmap -> roadmap.toDto() }
                ),
                CACHE_STATUS_CODE
            )
        }

        return when (val result = SafeApiCall.execute { homeApi.getTemplateTrendings() }) {
            is NetworkResult.Success -> {
                trendingRoadmapDao?.replaceAll(
                    result.data.trendings.map { roadmap -> roadmap.toEntity() }
                )
                syncManager?.markSynced(SyncDataType.TEMPLATE_TRENDINGS, serverVersions)
                result
            }

            is NetworkResult.Error -> {
                if (cachedTrendings.isNotEmpty()) {
                    NetworkResult.Success(
                        HomeTemplateTrendingsResponseDto(
                            total = cachedTrendings.size,
                            trendings = cachedTrendings.map { roadmap -> roadmap.toDto() }
                        ),
                        CACHE_STATUS_CODE
                    )
                } else {
                    result
                }
            }
        }
    }

    private fun List<com.rmap.mobile.features.roadmap.data.local.entity.TemplateCategoryEntity>
        .toHomeCategoriesDto(): TemplateCategoriesResponseDto {
        return TemplateCategoriesResponseDto(
            total = size,
            categories = map { category -> category.toHomeDto() }
        )
    }

    private companion object {
        const val CACHE_STATUS_CODE = 200
    }
}
