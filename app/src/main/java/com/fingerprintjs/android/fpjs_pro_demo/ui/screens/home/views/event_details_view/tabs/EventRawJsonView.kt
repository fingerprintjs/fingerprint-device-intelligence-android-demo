package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppColors
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.utils.ShowPreview

@Composable
fun EventRawJsonView(
    modifier: Modifier,
    code: String,
) {
    val hightlightedCode = highlightSyntax(code)
    Column(modifier = modifier.padding(start = 16.dp)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .wrapContentWidth(),
                textAlign = TextAlign.End,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                style = AppTheme.extendedTheme.typography.codeNormal,
                text = lineNumbersString(code)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState()),
            ) {
                SelectionContainer {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                        style = AppTheme.extendedTheme.typography.codeNormal,
                        softWrap = false,
                        lineHeight = 18.sp,
                        text = hightlightedCode,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }
}

/**
 * This handles the basic highlighting we need. If we start to get other requests,
 * this function should change to something that does precise JSON parsing.
 */
private fun highlightSyntax(s: String): AnnotatedString  {
    var finalString = AnnotatedString("")
    val startOfObjectOrArray = Regex("^\\s*[{\\[].*")
    val highlightStyle = SpanStyle(
        color = AppColors.Orange400,
    )

    s.lines().forEach {
        val splits = it.split("\":", limit = 2)
        // Add the first part of the line
        finalString += AnnotatedString(splits[0])
        if (splits.size > 1) {
            // Add what was removed in the split
            finalString += AnnotatedString("\":")
            val part2 = splits[1]
            // If this is pointing to an object or array, don't highlight that
            if (part2.matches(startOfObjectOrArray)) {
                finalString += AnnotatedString(part2)
            } else {
                finalString += AnnotatedString(part2.removeSuffix(","), highlightStyle)
                if (part2.endsWith(',')) {
                    finalString += AnnotatedString(",")
                }
            }
        }
        finalString += AnnotatedString("\n")
    }

    return finalString
}
private fun lineNumbersString(from: String): String =
    (1..from.lines().count())
        .joinToString(separator = "\n") { it.toString() }

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    ShowPreview {
        EventRawJsonView(
            modifier = Modifier.fillMaxSize(),
            code = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.rawJson.orEmpty(),
        )
    }
}
