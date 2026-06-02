package com.rmap.mobile.features.home.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rmap.mobile.features.home.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesRecentSearchRepository(
    context: Context
) : RecentSearchRepository {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val searchListType = object : TypeToken<List<String>>() {}.type
    private val _recentSearches = MutableStateFlow(readSearches())

    override val recentSearches: Flow<List<String>> = _recentSearches.asStateFlow()

    override suspend fun saveSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return

        val updatedSearches = (listOf(normalizedQuery) + readSearches().filterNot {
            it.equals(normalizedQuery, ignoreCase = true)
        }).take(MAX_RECENT_SEARCHES)
        writeSearches(updatedSearches)
    }

    override suspend fun removeSearch(query: String) {
        val normalizedQuery = query.trim()
        val updatedSearches = readSearches().filterNot {
            it.equals(normalizedQuery, ignoreCase = true)
        }
        writeSearches(updatedSearches)
    }

    override suspend fun clearSearches() {
        writeSearches(emptyList())
    }

    private fun readSearches(): List<String> {
        val storedSearches = sharedPreferences.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<String>>(storedSearches, searchListType)
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase() }
                .take(MAX_RECENT_SEARCHES)
        }.getOrDefault(emptyList())
    }

    private fun writeSearches(searches: List<String>) {
        sharedPreferences.edit()
            .putString(KEY_RECENT_SEARCHES, gson.toJson(searches))
            .apply()
        _recentSearches.value = searches
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_recent_searches"
        const val KEY_RECENT_SEARCHES = "recent_searches"
        const val MAX_RECENT_SEARCHES = 5
    }
}
