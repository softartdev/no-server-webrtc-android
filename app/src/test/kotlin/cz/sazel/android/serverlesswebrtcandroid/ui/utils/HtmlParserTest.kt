package cz.sazel.android.serverlesswebrtcandroid.ui.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test for HtmlParser to verify HTML color extraction functionality.
 */
class HtmlParserTest {

    @Test
    fun `extractColorFromHtml should extract debug color`() {
        val htmlText = """<font color="#673AB7">Debug message</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#673AB7", result)
    }

    @Test
    fun `extractColorFromHtml should extract success color`() {
        val htmlText = """<font color="#009900">Success message</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#009900", result)
    }

    @Test
    fun `extractColorFromHtml should extract info color`() {
        val htmlText = """<font color="#000099">Info message</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#000099", result)
    }

    @Test
    fun `extractColorFromHtml should extract error color`() {
        val htmlText = """<font color="#990000">Error message</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#990000", result)
    }

    @Test
    fun `extractColorFromHtml should return null for plain text`() {
        val htmlText = "Plain message without HTML"
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertNull(result)
    }

    @Test
    fun `extractColorFromHtml should return null for invalid HTML`() {
        val htmlText = """<font>Message without color</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertNull(result)
    }

    @Test
    fun `extractColorFromHtml should handle lowercase hex colors`() {
        val htmlText = """<font color="#abcdef">Message with lowercase hex</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#abcdef", result)
    }

    @Test
    fun `extractColorFromHtml should handle uppercase hex colors`() {
        val htmlText = """<font color="#ABCDEF">Message with uppercase hex</font>"""
        val result = HtmlParser.extractColorFromHtml(htmlText)
        assertEquals("#ABCDEF", result)
    }
}