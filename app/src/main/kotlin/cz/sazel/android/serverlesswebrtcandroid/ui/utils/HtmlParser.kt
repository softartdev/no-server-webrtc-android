package cz.sazel.android.serverlesswebrtcandroid.ui.utils

import android.text.Html
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Utility object for parsing HTML content to AnnotatedString while preserving colors.
 */
object HtmlParser {
    
    // Known color patterns used in console messages
    private val COLOR_MAPPINGS = mapOf(
        "#673AB7" to Color(0xFF673AB7), // Debug/purple color
        "#009900" to Color(0xFF009900), // Success/green color
        "#000099" to Color(0xFF000099), // Info/blue color
        "#990000" to Color(0xFF990000)  // Error/red color
    )
    
    /**
     * Parse HTML formatted text and convert to AnnotatedString to preserve colors.
     * Handles <font color="...">text</font> tags used by the console.
     */
    fun parseHtmlToAnnotatedString(htmlText: String): AnnotatedString {
        return buildAnnotatedString {
            val cleanText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString()
            
            // Find the color in the HTML text
            val colorHex = extractColorFromHtml(htmlText)
            val color = COLOR_MAPPINGS[colorHex]
            
            if (color != null) {
                withStyle(SpanStyle(color = color)) {
                    append(cleanText)
                }
            } else {
                // Default color - no special formatting
                append(cleanText)
            }
        }
    }
    
    /**
     * Extract color hex value from HTML font tag.
     * Returns null if no color is found.
     */
    internal fun extractColorFromHtml(htmlText: String): String? {
        val colorRegex = """<font color="(#[0-9A-Fa-f]{6})">""".toRegex()
        val matchResult = colorRegex.find(htmlText)
        return matchResult?.groupValues?.get(1)
    }
}