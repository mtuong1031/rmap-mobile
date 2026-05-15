package com.rmap.mobile.features.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppNavigationBar
import com.rmap.mobile.core.ui.components.BackgroundDecorator
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.ExperienceCard
import com.rmap.mobile.features.profile.presentation.components.ProfileCard
import com.rmap.mobile.features.profile.presentation.components.ProfileHeader
import com.rmap.mobile.features.profile.presentation.components.SettingType
import com.rmap.mobile.features.profile.presentation.components.SettingsSection
import com.rmap.mobile.features.profile.presentation.components.StatsSection
import com.rmap.mobile.navigation.NavBarDestination

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onSettingClick: (SettingType) -> Unit,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.More
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundDecorator(
                scrollOffsetY = scrollY,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 48.dp,
                    bottom = innerPadding.calculateBottomPadding() + 28.dp
                ),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileHeader(
                        title = stringResource(id = R.string.profile_header_title)
                    )
                }

                item {
                    ProfileCard(
                        avatarUrl = uiState.avatarUrl,
                        name = uiState.name,
                        role = uiState.role,
                        onEditClick = onEditProfile
                    )
                }

                item {
                    ExperienceCard(xp = uiState.xp)
                }

                item {
                    StatsSection(
                        streak = uiState.streak,
                        certificates = uiState.certificates
                    )
                }

                item {
                    SettingsSection(onItemClick = onSettingClick)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                name = "Thinh Duy",
                role = "Aspiring Frontend Developer",
                avatarUrl = "",
                xp = 450,
                streak = 5,
                certificates = 2
            ),
            onEditProfile = {},
            onSettingClick = {},
            onDestinationSelected = {}
        )
    }
}
