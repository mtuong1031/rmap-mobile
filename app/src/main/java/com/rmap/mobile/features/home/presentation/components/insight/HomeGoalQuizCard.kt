package com.rmap.mobile.features.home.presentation.components.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeGoalQuizCardShape = RoundedCornerShape(Dimens.cardRadiusXl)
private val HomeGoalQuizDecorativeIconSize = 96.dp

@Composable
fun HomeGoalQuizCard(
    title: String,
    description: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(shape = HomeGoalQuizCardShape)
            .clip(HomeGoalQuizCardShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.16f),
                shape = HomeGoalQuizCardShape
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Dimens.spacingXl, y = (-26).dp)
                .size(HomeGoalQuizDecorativeIconSize)
                .alpha(0.2f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Text(
                text = description,
                modifier = Modifier.fillMaxWidth(0.86f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            )

            RMapButton(
                text = actionText,
                onClick = onActionClick,
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.XSmall,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                },
                modifier = Modifier.padding(top = Dimens.spacingSm)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeGoalQuizCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeGoalQuizCard(
                title = "Not sure which path fits you?",
                description = "Answer a few questions and RMap will suggest your first roadmap.",
                actionText = "Take goal quiz",
                onActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390)
@Composable
private fun HomeGoalQuizCardDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeGoalQuizCard(
                title = "Not sure which path fits you?",
                description = "Answer a few questions and RMap will suggest your first roadmap.",
                actionText = "Take goal quiz",
                onActionClick = {}
            )
        }
    }
}
