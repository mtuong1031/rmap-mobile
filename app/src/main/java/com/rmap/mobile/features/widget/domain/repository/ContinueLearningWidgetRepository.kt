package com.rmap.mobile.features.widget.domain.repository

import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot

interface ContinueLearningWidgetRepository {
    fun getSnapshot(): ContinueLearningWidgetSnapshot

    suspend fun saveSnapshot(snapshot: ContinueLearningWidgetSnapshot): Result<Unit>
}
