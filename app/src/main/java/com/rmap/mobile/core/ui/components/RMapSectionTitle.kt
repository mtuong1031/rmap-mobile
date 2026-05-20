package com.rmap.mobile.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun RMapSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            lineHeight = 27.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun RMapSectionTitlePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapSectionTitle(text = "Today's Learning Plan")
    }
}
