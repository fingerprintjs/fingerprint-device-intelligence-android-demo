package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme

object LinkableText {
    @Stable
    data class Link(
        val mask: String,
        val handler: () -> Unit,
    )
}

@Composable
fun LinkableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    links: List<LinkableText.Link> = emptyList(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
) {
    // copied from "Text" function
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(color = textColor),
            start = 0,
            end = text.length
        )
        links.forEach { link ->
            val startIndex = text.indexOf(link.mask)
            val endIndex = startIndex + link.mask.length
            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = URL_TAG,
                    styles = TextLinkStyles(
                        style = SpanStyle(color = linkColor)
                    ),
                    linkInteractionListener = object : LinkInteractionListener {
                        override fun onClick(unused: LinkAnnotation) {
                            link.handler.invoke()
                        }
                    }
                ),
                start = startIndex,
                end = endIndex,
            )
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = style.merge(textAlign = textAlign),
        inlineContent = inlineContent,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Preview
@Composable
private fun WithLink() {
    AppTheme {
        LinkableText(
            text = buildAnnotatedString { append("Please contact support if you have any issues.") },
            links = listOf(
                LinkableText.Link(
                    mask = "contact support",
                    handler = {},
                )
            ),
        )
    }
}

private const val URL_TAG = "URL"
