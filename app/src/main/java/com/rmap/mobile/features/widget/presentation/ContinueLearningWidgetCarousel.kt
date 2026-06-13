package com.rmap.mobile.features.widget.presentation

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.rmap.mobile.core.utils.RMapAppGraph

internal val WidgetCarouselIndexKey = intPreferencesKey("learning_plan_index")

internal enum class WidgetCarouselDirection {
    Previous,
    Next
}

internal fun updatedCarouselIndex(
    currentIndex: Int,
    itemCount: Int,
    direction: WidgetCarouselDirection
): Int {
    if (itemCount <= 1) return 0
    val normalizedIndex = currentIndex.coerceIn(0, itemCount - 1)
    return when (direction) {
        WidgetCarouselDirection.Previous -> {
            if (normalizedIndex == 0) itemCount - 1 else normalizedIndex - 1
        }
        WidgetCarouselDirection.Next -> (normalizedIndex + 1) % itemCount
    }
}

class PreviousLearningPlanAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateCarousel(context, glanceId, WidgetCarouselDirection.Previous)
    }
}

class NextLearningPlanAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateCarousel(context, glanceId, WidgetCarouselDirection.Next)
    }
}

private suspend fun updateCarousel(
    context: Context,
    glanceId: GlanceId,
    direction: WidgetCarouselDirection
) {
    RMapAppGraph.initialize(context.applicationContext)
    val itemCount = RMapAppGraph.continueLearningWidgetRepository
        .getSnapshot()
        .learningPlans
        .size

    updateAppWidgetState(context, glanceId) { preferences ->
        preferences[WidgetCarouselIndexKey] = updatedCarouselIndex(
            currentIndex = preferences[WidgetCarouselIndexKey] ?: 0,
            itemCount = itemCount,
            direction = direction
        )
    }
    ContinueLearningWidget().update(context, glanceId)
}
