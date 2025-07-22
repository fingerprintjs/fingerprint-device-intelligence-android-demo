package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.compose.ui.text.AnnotatedString
import junit.framework.TestCase
import org.junit.Test

class JSONSyntaxHighlighterTests {
    @Test
    fun givenJson_spansMatchAsExpected() {
        val jsonEntry = """
            {
                "data": {
                    "a": "alice",
                    "b": "bob",
                    "c": [
                       "charlie",
                       "cat"
                    ],
                    "d": "dog"
                },
                "number": 5,
                "boolean": false,
                "string": "Hello World"
            }
            """
        val result = JSONSyntaxHighlighter(jsonEntry).highlighted()
        TestCase.assertEquals(jsonEntry, result.text)
        validateSpans(
            result,
            listOf(
                "\"alice\"",
                "\"bob\"",
                "\"charlie\"",
                "\"cat\"",
                "\"dog\"",
                "5",
                "false",
                "\"Hello World\"",
            )
        )
    }

    @Test
    fun givenJsonWithNoHightlights_spanCountIs0() {
        val jsonEntry = """
            {
                "data": {
                    "empty2": {
                    }
                },
                "array": [
                ]
            }
            """

        val result = JSONSyntaxHighlighter(jsonEntry).highlighted()
        TestCase.assertEquals(jsonEntry, result.text)
        validateSpans(result, listOf())
    }

    @Test
    fun givenAbsoluteGarbage_returnStringMatches() {
        val jsonEntry = """
            }}asdfa[]sd]4[
            """

        val result = JSONSyntaxHighlighter(jsonEntry).highlighted()
        TestCase.assertEquals(jsonEntry, result.text)
    }

    @Test
    fun givenJsonWithEmojis_spansMatchAsExpected() {
        // Checking unicode support
        val jsonEntry = """
            {
                "🏢": "🫆"
            }
            """
        val result = JSONSyntaxHighlighter(jsonEntry).highlighted()
        TestCase.assertEquals(jsonEntry, result.text)
        validateSpans(
            result,
            listOf(
                "\"🫆\"",
            )
        )
    }

    fun validateSpans(s: AnnotatedString, expectedSpans: List<String>) {
        TestCase.assertEquals(expectedSpans.size, s.spanStyles.size)
        s.spanStyles.forEachIndexed { i, span ->
            val text = s.text.substring(span.start, span.end).trimStart()
            TestCase.assertEquals(expectedSpans[i], text)
        }
    }
}