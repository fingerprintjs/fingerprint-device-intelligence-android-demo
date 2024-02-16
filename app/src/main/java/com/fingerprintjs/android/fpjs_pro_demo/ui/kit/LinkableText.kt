package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
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
    text: String,
    modifier: Modifier = Modifier,
    links: List<LinkableText.Link> = emptyList(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Unspecified,
) {
    // copied from "Text" function
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val helperAnnotationToLink = remember(links) {
        links
            .mapIndexed { index, link -> index.toString() to link }
            .toMap()
    }

    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(color = textColor),
            start = 0,
            end = text.length
        )
        for ((annotation, link) in helperAnnotationToLink) {
            val startIndex = text.indexOf(link.mask)
            val endIndex = startIndex + link.mask.length
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = URL_TAG,
                annotation = annotation,
                start = startIndex,
                end = endIndex
            )
        }
    }

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = style.merge(textAlign = textAlign),
        onClick = {
            annotatedString
                .getStringAnnotations(URL_TAG, it, it)
                .firstOrNull()?.let { helperAnnotation ->
                    helperAnnotationToLink.get(helperAnnotation.item)?.handler?.invoke()
                }
        }
    )
}

@Preview
@Composable
private fun WithLink() {
    AppTheme {
        LinkableText(
            text = "Please contact support if you have any issues.",
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