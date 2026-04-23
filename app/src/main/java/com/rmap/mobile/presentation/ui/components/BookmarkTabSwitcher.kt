package com.rmap.mobile.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.presentation.ui.theme.RMapTheme

@Composable
fun BookmarkTabSwitcher(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        val tabCount = tabs.size.coerceAtLeast(1)
        val density = LocalDensity.current
        val tabWidth = with(density) { (constraints.maxWidth / tabCount).toDp() }

        val indicatorWidth = 60.dp

        val indicatorOffset by animateDpAsState(
            targetValue = (tabWidth * selectedIndex) + (tabWidth / 2) - (indicatorWidth / 2),
            animationSpec = tween(durationMillis = 300),
            label = "indicatorOffset"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else Color.Gray,
                    animationSpec = tween(durationMillis = 300),
                    label = "textColor"
                )

                val textSize by animateFloatAsState(
                    targetValue = if (isSelected) 22f else 16f,
                    animationSpec = tween(durationMillis = 300),
                    label = "textSize"
                )

                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .padding(vertical = 16.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = textSize.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = textColor,
                        maxLines = 1,
                        modifier = Modifier.alpha(if (isSelected) 1f else 0.6f)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF298CF7))
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390)
@Composable
private fun BookmarkTabSwitcherPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        BookmarkTabSwitcher(
            tabs = listOf("Saved Roadmaps", "Saved Skills"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}
