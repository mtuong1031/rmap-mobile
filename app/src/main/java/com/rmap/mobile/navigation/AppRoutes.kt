package com.rmap.mobile.navigation

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
    const val ROADMAP_DETAIL = "roadmap_detail/{$ROADMAP_ID_ARG}"

    fun roadmapDetail(roadmapId: String): String = "roadmap_detail/$roadmapId"
}
