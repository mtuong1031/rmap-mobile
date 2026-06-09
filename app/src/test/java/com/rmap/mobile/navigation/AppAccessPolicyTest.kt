package com.rmap.mobile.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppAccessPolicyTest {
    @Test
    fun `guest can browse public discovery routes`() {
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.HOME, isAuthenticated = false))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.HOME_SEARCH, isAuthenticated = false))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.EXPLORE, isAuthenticated = false))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.PROFILE, isAuthenticated = false))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.skillDetail("skill-1"), isAuthenticated = false))
        assertTrue(
            AppAccessPolicy.canAccess(
                AppRoutes.roadmapDetail("template-1"),
                isAuthenticated = false
            )
        )
    }

    @Test
    fun `guest cannot access personalized routes`() {
        assertFalse(AppAccessPolicy.canAccess(AppRoutes.MY_ROADMAP, isAuthenticated = false))
        assertFalse(AppAccessPolicy.canAccess(AppRoutes.AI_ROADMAP, isAuthenticated = false))
        assertFalse(
            AppAccessPolicy.canAccess(
                AppRoutes.roadmapLearning(
                    roadmapId = "roadmap-1",
                    nodeId = "node-1",
                    skillId = "skill-1",
                    isCompleted = false
                ),
                isAuthenticated = false
            )
        )
    }

    @Test
    fun `authenticated user can access every destination`() {
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.MY_ROADMAP, isAuthenticated = true))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.AI_ROADMAP, isAuthenticated = true))
        assertTrue(AppAccessPolicy.canAccess(AppRoutes.PROFILE, isAuthenticated = true))
    }

    @Test
    fun `bottom navigation maps to expected routes`() {
        assertEquals(AppRoutes.HOME, AppAccessPolicy.routeFor(NavBarDestination.Home))
        assertEquals(AppRoutes.EXPLORE, AppAccessPolicy.routeFor(NavBarDestination.Explore))
        assertEquals(AppRoutes.MY_ROADMAP, AppAccessPolicy.routeFor(NavBarDestination.MyRoadmap))
        assertEquals(AppRoutes.AI_ROADMAP, AppAccessPolicy.routeFor(NavBarDestination.AiAssistant))
        assertEquals(AppRoutes.PROFILE, AppAccessPolicy.routeFor(NavBarDestination.More))
    }
}
