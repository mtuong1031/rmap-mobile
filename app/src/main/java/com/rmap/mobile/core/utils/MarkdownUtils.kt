package com.rmap.mobile.core.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

/**
 * Utility to parse simple inline markdown (bold, italic, bold-italic, inline code)
 * into an [AnnotatedString] for rendering in Jetpack Compose [Text] components.
 */
fun parseMarkdownToAnnotatedString(
    text: String,
    codeTextColor: Color = Color.Unspecified,
    codeBackgroundColor: Color = Color.Unspecified
): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val n = text.length
        while (i < n) {
            // Check for inline code backtick `
            if (text[i] == '`') {
                val nextBacktick = text.indexOf('`', i + 1)
                if (nextBacktick != -1) {
                    val codeContent = text.substring(i + 1, nextBacktick)
                    pushStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = codeTextColor,
                            background = codeBackgroundColor,
                            fontSize = 13.sp
                        )
                    )
                    append(codeContent)
                    pop()
                    i = nextBacktick + 1
                    continue
                }
            }

            // Check for *** (bold italic)
            if (i + 2 < n && text[i] == '*' && text[i + 1] == '*' && text[i + 2] == '*') {
                val nextMatch = text.indexOf("***", i + 3)
                if (nextMatch != -1) {
                    val content = text.substring(i + 3, nextMatch)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                    i = nextMatch + 3
                    continue
                }
            }
            if (i + 2 < n && text[i] == '_' && text[i + 1] == '_' && text[i + 2] == '_') {
                val nextMatch = text.indexOf("___", i + 3)
                if (nextMatch != -1) {
                    val content = text.substring(i + 3, nextMatch)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                    i = nextMatch + 3
                    continue
                }
            }

            // Check for ** (bold)
            if (i + 1 < n && text[i] == '*' && text[i + 1] == '*') {
                val nextMatch = text.indexOf("**", i + 2)
                if (nextMatch != -1) {
                    val content = text.substring(i + 2, nextMatch)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(content)
                    pop()
                    i = nextMatch + 2
                    continue
                }
            }
            if (i + 1 < n && text[i] == '_' && text[i + 1] == '_') {
                val nextMatch = text.indexOf("__", i + 2)
                if (nextMatch != -1) {
                    val content = text.substring(i + 2, nextMatch)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(content)
                    pop()
                    i = nextMatch + 2
                    continue
                }
            }

            // Check for * (italic)
            if (text[i] == '*') {
                val nextMatch = text.indexOf('*', i + 1)
                if (nextMatch != -1) {
                    val content = text.substring(i + 1, nextMatch)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                    i = nextMatch + 1
                    continue
                }
            }
            if (text[i] == '_') {
                val nextMatch = text.indexOf('_', i + 1)
                if (nextMatch != -1) {
                    val content = text.substring(i + 1, nextMatch)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                    i = nextMatch + 1
                    continue
                }
            }

            // Otherwise, append character
            append(text[i])
            i++
        }
    }
}
