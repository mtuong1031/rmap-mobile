package com.rmap.mobile.features.profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.ProfileSettingsTopBar

import com.rmap.mobile.features.profile.domain.model.UserIntegration
import com.rmap.mobile.features.profile.presentation.viewmodel.ConnectedAccountsUiState

@Composable
fun ConnectedAccountsScreen(
    uiState: ConnectedAccountsUiState,
    onBackClick: () -> Unit,
    onConnectClick: (String) -> Unit,
    onDisconnectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            ProfileSettingsTopBar(
                titleResId = R.string.profile_setting_connected_accounts,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingXl,
                    top = Dimens.spacingXxl,
                    end = Dimens.spacingXl,
                    bottom = Dimens.spacingXxxl
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                item {
                    ConnectedAccountsCard(
                        uiState = uiState,
                        onConnectClick = onConnectClick,
                        onDisconnectClick = onDisconnectClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectedAccountsCard(
    uiState: ConnectedAccountsUiState,
    onConnectClick: (String) -> Unit,
    onDisconnectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                Text(
                    text = stringResource(id = R.string.connected_accounts_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.connected_accounts_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            SectionDivider()

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.integrations.forEachIndexed { index, account ->
                    AccountItemRow(
                        item = account,
                        onConnectClick = { onConnectClick(account.provider) },
                        onDisconnectClick = { onDisconnectClick(account.provider) }
                    )
                    
                    if (index < uiState.integrations.size - 1) {
                        SectionDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountItemRow(
    item: UserIntegration,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacingXl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        val iconResId = if (item.provider.lowercase() == "github") R.drawable.ic_logo_github else R.drawable.ic_logo_google
        val title = if (item.provider.lowercase() == "github") "GitHub" else "Google"
        val description = if (item.connected) item.providerEmail ?: "Connected" else "No $title account connected."

        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Dimens.radiusSm))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconLg),
                tint = Color.Unspecified
            )
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                StatusBadge(isConnected = item.connected)
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Action Button
        RMapButton(
            text = stringResource(
                id = if (item.connected) {
                    R.string.connected_accounts_action_disconnect
                } else {
                    R.string.connected_accounts_action_connect
                }
            ),
            onClick = if (item.connected) onDisconnectClick else onConnectClick,
            variant = RMapButtonVariant.Outline,
            size = RMapButtonSize.Small,
            enabled = if (item.connected) item.canDisconnect else true
        )
    }
}

@Composable
private fun StatusBadge(isConnected: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (isConnected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isConnected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .padding(horizontal = Dimens.spacingSm, vertical = 2.dp)
    ) {
        Text(
            text = stringResource(
                id = if (isConnected) {
                    R.string.connected_accounts_status_connected
                } else {
                    R.string.connected_accounts_status_not_connected
                }
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun SectionDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.borderThin)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun ConnectedAccountsScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ConnectedAccountsScreen(
            uiState = ConnectedAccountsUiState(
                integrations = listOf(
                    UserIntegration(
                        provider = "github",
                        connected = false,
                        canDisconnect = false,
                        connectedAt = null,
                        providerEmail = null
                    ),
                    UserIntegration(
                        provider = "google",
                        connected = false,
                        canDisconnect = false,
                        connectedAt = null,
                        providerEmail = null
                    )
                )
            ),
            onBackClick = {},
            onConnectClick = {},
            onDisconnectClick = {}
        )
    }
}
