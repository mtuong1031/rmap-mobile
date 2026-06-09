package com.rmap.mobile.navigation

enum class AppAccessLevel {
    Public,
    Authenticated
}

object AppAccessPolicy {
    fun routeFor(destination: NavBarDestination): String {
        return when (destination) {
            NavBarDestination.Home -> AppRoutes.HOME
            NavBarDestination.Explore -> AppRoutes.EXPLORE
            NavBarDestination.MyRoadmap -> AppRoutes.MY_ROADMAP
            NavBarDestination.AiAssistant -> AppRoutes.AI_ROADMAP
            NavBarDestination.More -> AppRoutes.PROFILE
        }
    }

    fun accessLevel(route: String): AppAccessLevel {
        return when {
            route == AppRoutes.MY_ROADMAP -> AppAccessLevel.Authenticated
            route == AppRoutes.AI_ROADMAP -> AppAccessLevel.Authenticated
            route == AppRoutes.NOTIFICATION_SETTINGS -> AppAccessLevel.Authenticated
            route.startsWith("roadmap_learning/") -> AppAccessLevel.Authenticated
            route.startsWith("roadmap_milestone/") -> AppAccessLevel.Authenticated
            else -> AppAccessLevel.Public
        }
    }

    fun canAccess(
        route: String,
        isAuthenticated: Boolean
    ): Boolean {
        return isAuthenticated || accessLevel(route) == AppAccessLevel.Public
    }
}
