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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationRoute = intent.toNotificationRoute()
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
                        initialNotificationRoute = notificationRoute
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationRoute = intent.toNotificationRoute()
    }

    private fun Intent?.toNotificationRoute(): String? {
        if (this == null) return null
        val destination = getStringExtra(EXTRA_NOTIFICATION_DESTINATION)
        return when (destination) {
            DESTINATION_ROADMAP_DETAIL -> {
                val roadmapId = getStringExtra(EXTRA_ROADMAP_ID)?.takeIf { it.isNotBlank() }
                roadmapId?.let(AppRoutes::roadmapDetail)
            }
            else -> null
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION_DESTINATION = "com.rmap.mobile.extra.NOTIFICATION_DESTINATION"
        const val EXTRA_ROADMAP_ID = "com.rmap.mobile.extra.ROADMAP_ID"
        const val DESTINATION_ROADMAP_DETAIL = "roadmap_detail"
    }
}
