package com.rmap.mobile.features.home.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    val recentSearches: Flow<List<String>>

    suspend fun saveSearch(query: String)
    suspend fun removeSearch(query: String)
    suspend fun clearSearches()
}
