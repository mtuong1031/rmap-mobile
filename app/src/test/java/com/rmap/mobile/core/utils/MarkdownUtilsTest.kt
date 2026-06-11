package com.rmap.mobile.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Test

class MarkdownUtilsTest {

    @Test
    fun `parseMarkdownToAnnotatedString parses bold text`() {
        val input = "This is **bold** text."
        val result = parseMarkdownToAnnotatedString(input)

        // Raw text should have markdown tags stripped
        assertEquals("This is bold text.", result.text)

        // Verify the bold styling span
        val spans = result.spanStyles
        assertEquals(1, spans.size)
        val span = spans[0]
        assertEquals(8, span.start)
        assertEquals(12, span.end)
        assertEquals(FontWeight.Bold, span.item.fontWeight)
    }

    @Test
    fun `parseMarkdownToAnnotatedString parses italic text`() {
        val input = "This is *italic* text."
        val result = parseMarkdownToAnnotatedString(input)

        assertEquals("This is italic text.", result.text)

        val spans = result.spanStyles
        assertEquals(1, spans.size)
        val span = spans[0]
        assertEquals(8, span.start)
        assertEquals(14, span.end)
        assertEquals(FontStyle.Italic, span.item.fontStyle)
    }

    @Test
    fun `parseMarkdownToAnnotatedString parses inline code`() {
        val input = "This is `code` text."
        val textColor = Color.Red
        val bgColor = Color.Yellow
        val result = parseMarkdownToAnnotatedString(
            text = input,
            codeTextColor = textColor,
            codeBackgroundColor = bgColor
        )

        assertEquals("This is code text.", result.text)

        val spans = result.spanStyles
        assertEquals(1, spans.size)
        val span = spans[0]
        assertEquals(8, span.start)
        assertEquals(12, span.end)
        assertEquals(FontFamily.Monospace, span.item.fontFamily)
        assertEquals(textColor, span.item.color)
        assertEquals(bgColor, span.item.background)
    }

    @Test
    fun `parseMarkdownToAnnotatedString parses multiple formats`() {
        val input = "This has **bold**, `code`, and *italic*."
        val result = parseMarkdownToAnnotatedString(input)

        assertEquals("This has bold, code, and italic.", result.text)

        val spans = result.spanStyles.sortedBy { it.start }
        assertEquals(3, spans.size)

        // Bold
        assertEquals(9, spans[0].start)
        assertEquals(13, spans[0].end)
        assertEquals(FontWeight.Bold, spans[0].item.fontWeight)

        // Code
        assertEquals(15, spans[1].start)
        assertEquals(19, spans[1].end)
        assertEquals(FontFamily.Monospace, spans[1].item.fontFamily)

        // Italic
        assertEquals(25, spans[2].start)
        assertEquals(31, spans[2].end)
        assertEquals(FontStyle.Italic, spans[2].item.fontStyle)
    }
}
