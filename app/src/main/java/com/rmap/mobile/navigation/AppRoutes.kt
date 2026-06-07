package com.rmap.mobile.navigation

object AppRoutes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val HOME_SEARCH = "home_search"
    const val MY_ROADMAP = "my_roadmap"
    const val EXPLORE = "explore"
    const val EXPLORE_CATEGORY_ARG = "category"
    const val EXPLORE_WITH_CATEGORY = "explore?$EXPLORE_CATEGORY_ARG={$EXPLORE_CATEGORY_ARG}"
    const val AI_ROADMAP = "ai_roadmap"
    const val PROFILE = "profile"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val ROADMAP_ID_ARG = "roadmapId"
    const val ROADMAP_NODE_ID_ARG = "nodeId"
    const val ROADMAP_DETAIL = "roadmap_detail/{$ROADMAP_ID_ARG}"
    const val ROADMAP_NODE_LEARNING =
        "roadmap_detail/{$ROADMAP_ID_ARG}/nodes/{$ROADMAP_NODE_ID_ARG}/learning"
    const val ROADMAP_NODE_QUIZ =
        "roadmap_detail/{$ROADMAP_ID_ARG}/nodes/{$ROADMAP_NODE_ID_ARG}/quiz"
    const val ROADMAP_DETAIL_REFRESH_RESULT = "roadmapDetailRefreshResult"
    const val LEARNING_NODE_REFRESH_RESULT = "learningNodeRefreshResult"

    fun roadmapDetail(roadmapId: String): String = "roadmap_detail/$roadmapId"
    fun explore(categoryId: String): String = "explore?$EXPLORE_CATEGORY_ARG=$categoryId"
    fun roadmapNodeLearning(
        roadmapId: String,
        nodeId: String
    ): String = "roadmap_detail/$roadmapId/nodes/$nodeId/learning"

    fun roadmapNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): String = "roadmap_detail/$roadmapId/nodes/$nodeId/quiz"
}
