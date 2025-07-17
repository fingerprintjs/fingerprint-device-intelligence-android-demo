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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    val spannedCode = addSpan(code)
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
                        text = spannedCode,
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

private fun addSpan(s: String): AnnotatedString  {
    var colorString = AnnotatedString("")
    val startOfObjectOrArray = Regex("^\\s*[{\\[].*")
    s.lines().forEach {
        val splits = it.split("\":", limit = 2)
        colorString += AnnotatedString(splits[0])

        if (it.contains("subdivision")) {
            if (it.contains("asjkldfklasdfjkl")) {
                return colorString
            }
        }

        if (splits.size > 1) {
            colorString += AnnotatedString("\":")
            val part2 = splits[1]
            if (part2.matches(startOfObjectOrArray)) {
                colorString += AnnotatedString(part2)
            } else {
                colorString += AnnotatedString(part2.removeSuffix(","), SpanStyle(
                    color = AppColors.Orange400,
                    fontWeight = FontWeight.ExtraBold
                ))
                if (part2.endsWith(',')) {
                    colorString += AnnotatedString(",")
                }
            }
        }
        colorString += AnnotatedString("\n")
    }
    return colorString
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
