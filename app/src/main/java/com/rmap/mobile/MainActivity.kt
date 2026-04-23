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
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.ui.theme.RMapTheme

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

                    when (selectedDestination) {
                        NavBarDestination.Home -> {
                            HomeScreen(
                                userName = "Thinh",
                                selectedDestination = selectedDestination,
                                onDestinationSelected = { selectedDestination = it }
                            )
                        }
                        NavBarDestination.Explore -> {
                            com.rmap.mobile.presentation.explore.ExploreScreen(
                                userName = "Thinh",
                                selectedDestination = selectedDestination,
                                onDestinationSelected = { selectedDestination = it }
                            )
                        }
                        else -> {
                            // Placeholder for other screens
                            HomeScreen(
                                userName = "Thinh",
                                selectedDestination = selectedDestination,
                                onDestinationSelected = { selectedDestination = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
