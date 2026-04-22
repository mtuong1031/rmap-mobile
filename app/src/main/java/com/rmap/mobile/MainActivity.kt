package com.rmap.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rmap.mobile.presentation.home.HomeScreen
import com.rmap.mobile.presentation.bookmarks.BookmarksScreen
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.ui.theme.RMapTheme
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedDestination by remember { mutableStateOf(NavBarDestination.Home) }

                    Crossfade(targetState = selectedDestination, label = "main_nav") { destination ->
                        when (destination) {
                            NavBarDestination.Home -> {
                                HomeScreen(
                                    userName = "Thinh",
                                    selectedDestination = destination,
                                    onDestinationSelected = { selectedDestination = it }
                                )
                            }
                            NavBarDestination.Bookmarks -> {
                                BookmarksScreen(
                                    userName = "Thinh",
                                    selectedDestination = destination,
                                    onDestinationSelected = { selectedDestination = it }
                                )
                            }
                            else -> {
                                // Empty state for other tabs temporarily
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}
