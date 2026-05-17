package com.rmap.mobile.navigation

object AppRoutes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val BOOKMARKS = "bookmarks"
    const val EXPLORE = "explore"
    const val PROFILE = "profile"
    const val ROADMAP_ID_ARG = "roadmapId"
    const val ROADMAP_DETAIL = "roadmap_detail/{$ROADMAP_ID_ARG}"

    fun roadmapDetail(roadmapId: String): String = "roadmap_detail/$roadmapId"
}
