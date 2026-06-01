package com.rmap.mobile.features.home.data.repository

import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.features.home.data.mapper.toHomeContent
import com.rmap.mobile.features.home.data.remote.HomeApi
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HomeRepositoryImpl(
    private val homeApi: HomeApi
) : HomeRepository {
    override suspend fun getHomeContent(): Result<HomeContent> {
        return coroutineScope {
            val dashboard = async { SafeApiCall.execute { homeApi.getDashboardHome() } }
            val recommendations = async { SafeApiCall.execute { homeApi.getTemplateRecommendations() } }
            val categories = async { SafeApiCall.execute { homeApi.getTemplateCategories() } }
            val trendings = async { SafeApiCall.execute { homeApi.getTemplateTrendings() } }

            val dashboardResult = dashboard.await()
            val recommendationsResult = recommendations.await()
            val categoriesResult = categories.await()
            val trendingsResult = trendings.await()

            val error = listOf(
                dashboardResult,
                recommendationsResult,
                categoriesResult,
                trendingsResult
            ).filterIsInstance<NetworkResult.Error>().firstOrNull()

            if (error != null) {
                Result.failure(error.toAppException())
            } else if (
                dashboardResult is NetworkResult.Success &&
                recommendationsResult is NetworkResult.Success &&
                categoriesResult is NetworkResult.Success &&
                trendingsResult is NetworkResult.Success
            ) {
                Result.success(
                    toHomeContent(
                        dashboard = dashboardResult.data,
                        recommendations = recommendationsResult.data,
                        categories = categoriesResult.data,
                        trendings = trendingsResult.data
                    )
                )
            } else {
                Result.failure(IllegalStateException("Unable to load home"))
            }
        }
    }
}
