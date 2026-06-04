package com.rmap.mobile.navigation

import android.net.Uri

object AppRoutes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val HOME_SEARCH = "home_search"
    const val BOOKMARKS = "bookmarks"
    const val EXPLORE = "explore"
    const val AI_ROADMAP = "ai_roadmap"
    const val PROFILE = "profile"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val ROADMAP_ID_ARG = "roadmapId"
    const val NODE_ID_ARG = "nodeId"
    const val SKILL_ID_ARG = "skillId"
    const val NODE_COMPLETED_ARG = "nodeCompleted"
    const val ROADMAP_DETAIL_REFRESH_RESULT = "roadmapDetailRefreshResult"
    const val LEARNING_NODE_REFRESH_RESULT = "learningNodeRefreshResult"
    const val ROADMAP_DETAIL = "roadmap_detail/{$ROADMAP_ID_ARG}"
    const val ROADMAP_LEARNING =
        "roadmap_learning/{$ROADMAP_ID_ARG}/{$NODE_ID_ARG}/{$SKILL_ID_ARG}/{$NODE_COMPLETED_ARG}"
    const val ROADMAP_NODE_QUIZ = "roadmap_learning/{$ROADMAP_ID_ARG}/{$NODE_ID_ARG}/quiz"

    fun roadmapDetail(roadmapId: String): String = "roadmap_detail/$roadmapId"

    fun roadmapLearning(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean
    ): String {
        return "roadmap_learning/${roadmapId.encodeRouteArg()}/${nodeId.encodeRouteArg()}/" +
            "${skillId.encodeRouteArg()}/$isCompleted"
    }

    fun roadmapNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): String {
        return "roadmap_learning/${roadmapId.encodeRouteArg()}/${nodeId.encodeRouteArg()}/quiz"
    }

    private fun String.encodeRouteArg(): String = Uri.encode(this)
}
