package com.rmap.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.navigation.AppRoutes
import com.rmap.mobile.navigation.RMapNavHost

class MainActivity : AppCompatActivity() {
    private var notificationRoute by mutableStateOf<String?>(null)
    private var initialMainTab by mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.toNavigationTarget().let { target ->
            notificationRoute = target.route
            initialMainTab = target.mainTab
        }
        RMapAppGraph.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            RMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    RMapNavHost(
                        navController = navController,
                        initialNotificationRoute = notificationRoute,
                        initialMainTab = initialMainTab
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.toNavigationTarget().let { target ->
            notificationRoute = target.route
            initialMainTab = target.mainTab
        }
    }

    private fun Intent?.toNavigationTarget(): NavigationTarget {
        if (this == null) return NavigationTarget()
        val destination = getStringExtra(EXTRA_NOTIFICATION_DESTINATION)
        return when (destination) {
            DESTINATION_AUTH -> NavigationTarget(route = AppRoutes.AUTH)
            DESTINATION_EXPLORE -> NavigationTarget(route = AppRoutes.EXPLORE)
            DESTINATION_HOME -> NavigationTarget(mainTab = HOME_TAB_INDEX)
            DESTINATION_MY_ROADMAP -> NavigationTarget(mainTab = MY_ROADMAP_TAB_INDEX)
            DESTINATION_ROADMAP_DETAIL -> {
                val roadmapId = getStringExtra(EXTRA_ROADMAP_ID)?.takeIf { it.isNotBlank() }
                NavigationTarget(route = roadmapId?.let(AppRoutes::roadmapDetail))
            }
            else -> NavigationTarget()
        }
    }

    private data class NavigationTarget(
        val route: String? = null,
        val mainTab: Int? = null
    )

    companion object {
        const val EXTRA_NOTIFICATION_DESTINATION = "com.rmap.mobile.extra.NOTIFICATION_DESTINATION"
        const val EXTRA_ROADMAP_ID = "com.rmap.mobile.extra.ROADMAP_ID"
        const val DESTINATION_AUTH = "auth"
        const val DESTINATION_EXPLORE = "explore"
        const val DESTINATION_ROADMAP_DETAIL = "roadmap_detail"
        const val DESTINATION_HOME = "home"
        const val DESTINATION_MY_ROADMAP = "my_roadmap"

        private const val HOME_TAB_INDEX = 0
        private const val MY_ROADMAP_TAB_INDEX = 1
    }
}
