package com.rmap.mobile.features.home.data.repository

import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.features.home.data.mapper.toHomeContent
import com.rmap.mobile.features.home.data.mapper.toSearchDomain
import com.rmap.mobile.features.home.data.remote.HomeApi
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HomeRepositoryImpl(
    private val homeApi: HomeApi
) : HomeRepository {
    override suspend fun getHomeContent(includePersonalDashboard: Boolean): Result<HomeContent> {
        return coroutineScope {
            val dashboard = if (includePersonalDashboard) {
                async { SafeApiCall.execute { homeApi.getDashboardHome() } }
            } else {
                null
            }
            val templates = async {
                SafeApiCall.execute {
                    homeApi.getPublicTemplates(
                        page = FIRST_PAGE,
                        perPage = PUBLIC_TEMPLATE_LIMIT
                    )
                }
            }

            val templatesResult = templates.await()
            val dashboardResult = dashboard?.await()

            when (templatesResult) {
                is NetworkResult.Success -> Result.success(
                    toHomeContent(
                        dashboard = (dashboardResult as? NetworkResult.Success)?.data,
                        templates = templatesResult.data
                    )
                )
                is NetworkResult.Error -> Result.failure(templatesResult.toAppException())
            }
        }
    }

    override suspend fun searchDashboard(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ): Result<HomeSearchResult> {
        return when (val result = SafeApiCall.execute {
            homeApi.getPublicTemplates(
                page = FIRST_PAGE,
                perPage = PUBLIC_TEMPLATE_LIMIT
            )
        }) {
            is NetworkResult.Success -> Result.success(
                result.data.toSearchDomain(
                    query = query,
                    roadmapPage = roadmapPage,
                    roadmapPageSize = SEARCH_ROADMAP_PAGE_SIZE,
                    skillPage = skillPage
                )
            )
            is NetworkResult.Error -> Result.failure(result.toAppException())
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val PUBLIC_TEMPLATE_LIMIT = 100
        const val SEARCH_ROADMAP_PAGE_SIZE = 5
    }
}
