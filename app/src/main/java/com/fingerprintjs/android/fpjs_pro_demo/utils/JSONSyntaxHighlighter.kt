package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppColors

class JSONSyntaxHighlighter(private val json: String) {
    private val jsonTokenizer = JSONTokenizer(json)

    fun highlighted(): AnnotatedString {
        try {
            val highlightStyle = SpanStyle(
                color = AppColors.Orange400,
                fontWeight = FontWeight.W600
            )

            val spans = mutableListOf<AnnotatedString.Range<SpanStyle>>()

            var previousToken: JSONTokenizer.Token? = null
            var token = jsonTokenizer.nextToken()
            while (token != null) {
                if (
                    previousToken != null &&
                    previousToken.type.isPropertyValue &&
                    token.type.isPropertyValueTerminator
                ) {
                    val propertyValueRange = previousToken.range
                    spans.add(
                        AnnotatedString.Range(
                            highlightStyle,
                            propertyValueRange.start,
                            propertyValueRange.endInclusive + 1
                        )
                    )
                }
                previousToken = token
                token = jsonTokenizer.nextToken()
            }
            return AnnotatedString(text = json, spanStyles = spans)
        } catch(_: Exception) {
            return AnnotatedString(json)
        }
    }

    private class JSONTokenizer(private val json: String) {
        enum class TokenType {
            BLOCK_START, BLOCK_END, ARRAY_START, ARRAY_END, LITERAL, STRING, COMMA, COLON;

            val isPropertyValue: Boolean
                get() = this == LITERAL || this == STRING

            val isPropertyValueTerminator: Boolean
                get() = this == BLOCK_END || this == ARRAY_END || this == COMMA
        }

        data class Token(val type: TokenType, val range: IntRange)

        private enum class State { START, STRING, LITERAL }
        private var startIndex = 0

        private val curlyBracketOpen = '{'
        private val curlyBracketClose = '}'
        private val squareBracketOpen = '['
        private val squareBracketClose = ']'
        private val comma = ','
        private val colon = ':'
        private val quote = '"'
        private val escape = '\\'
        private val literalSeparators = setOf(
            curlyBracketOpen, curlyBracketClose, squareBracketOpen, squareBracketClose, comma, colon, quote
        )

        private var index = 0
        private var state = State.START
        private var isStringEscapeSequence = false

        fun nextToken(): Token? {
            while (index < json.length) {
                val currentIndex = index
                val character = json[currentIndex]
                index++
                when (state) {
                    State.START -> {
                        if (character.isWhitespace()) continue
                        when (character) {
                            curlyBracketOpen -> return Token(TokenType.BLOCK_START, currentIndex..currentIndex)
                            curlyBracketClose -> return Token(TokenType.BLOCK_END, currentIndex..currentIndex)
                            squareBracketOpen -> return Token(TokenType.ARRAY_START, currentIndex..currentIndex)
                            squareBracketClose -> return Token(TokenType.ARRAY_END, currentIndex..currentIndex)
                            comma -> return Token(TokenType.COMMA, currentIndex..currentIndex)
                            colon -> return Token(TokenType.COLON, currentIndex..currentIndex)
                            quote -> { state = State.STRING; startIndex = currentIndex }
                            else -> { state = State.LITERAL; startIndex = currentIndex }
                        }
                    }
                    State.LITERAL -> {
                        if (character in literalSeparators || character.isWhitespace()) {
                            index--
                            state = State.START
                            return Token(TokenType.LITERAL, startIndex until currentIndex)
                        }
                    }
                    State.STRING -> {
                        if (isStringEscapeSequence) {
                            isStringEscapeSequence = false
                        } else if (character == escape) {
                            isStringEscapeSequence = true
                        } else if (character == quote) {
                            state = State.START
                            return Token(TokenType.STRING, startIndex until index)
                        }
                    }
                }
            }
            return null
        }
    }
}