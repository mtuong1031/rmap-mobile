package com.rmap.mobile.navigation

import android.net.Uri

object AppRoutes {
    // Root routes
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_TABS = "main_tabs"

    // Authentication
    const val AUTH = "auth"
    const val HOME = "home"
    const val HOME_SEARCH = "home_search"
    const val MY_ROADMAP = "my_roadmap"
    const val EXPLORE = "explore"
    const val AI_ROADMAP = "ai_roadmap"
    const val PROFILE = "profile"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val ROADMAP_ID_ARG = "roadmapId"
    const val NODE_ID_ARG = "nodeId"
    const val MILESTONE_ID_ARG = "milestoneId"
    const val SKILL_ID_ARG = "skillId"
    const val NODE_COMPLETED_ARG = "nodeCompleted"
    const val ROADMAP_DETAIL_REFRESH_RESULT = "roadmapDetailRefreshResult"
    const val ROADMAP_DETAIL_SCROLL_REQUEST = "roadmapDetailScrollRequest"
    const val ROADMAP_DETAIL_SCROLL_HERO = "hero"
    const val ROADMAP_DETAIL_SCROLL_IN_PROGRESS_GROUP = "inProgressGroup"
    const val LEARNING_NODE_REFRESH_RESULT = "learningNodeRefreshResult"
    const val GROUP_TITLE_ARG = "groupTitle"
    const val ROADMAP_DETAIL = "roadmap_detail/{$ROADMAP_ID_ARG}"
    const val ROADMAP_LEARNING =
        "roadmap_learning/{$ROADMAP_ID_ARG}/{$NODE_ID_ARG}/{$SKILL_ID_ARG}/{$NODE_COMPLETED_ARG}?$GROUP_TITLE_ARG={$GROUP_TITLE_ARG}"
    const val ROADMAP_NODE_QUIZ = "roadmap_learning/{$ROADMAP_ID_ARG}/{$NODE_ID_ARG}/quiz"
    const val ROADMAP_MILESTONE = "roadmap_milestone/{$ROADMAP_ID_ARG}/{$MILESTONE_ID_ARG}"

    fun roadmapDetail(roadmapId: String): String = "roadmap_detail/$roadmapId"

    fun roadmapLearning(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean,
        groupTitle: String? = null
    ): String {
        val base = "roadmap_learning/${roadmapId.encodeRouteArg()}/${nodeId.encodeRouteArg()}/" +
            "${skillId.encodeRouteArg()}/$isCompleted"
        return if (groupTitle != null) {
            "$base?$GROUP_TITLE_ARG=${groupTitle.encodeRouteArg()}"
        } else {
            base
        }
    }

    fun roadmapNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): String {
        return "roadmap_learning/${roadmapId.encodeRouteArg()}/${nodeId.encodeRouteArg()}/quiz"
    }

    fun roadmapMilestone(
        roadmapId: String,
        milestoneId: String
    ): String {
        return "roadmap_milestone/${roadmapId.encodeRouteArg()}/${milestoneId.encodeRouteArg()}"
    }

    private fun String.encodeRouteArg(): String = Uri.encode(this)
}
