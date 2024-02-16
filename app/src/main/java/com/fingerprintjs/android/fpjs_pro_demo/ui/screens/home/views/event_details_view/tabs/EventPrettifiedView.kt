package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.Shimmable
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.ShimmableState
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import com.fingerprintjs.android.fpjs_pro_demo.utils.copyOnLongPress

data class PrettifiedProperty(
    val name: String,
    val value: String?,
)

@Composable
fun EventPrettifiedView(
    modifier: Modifier,
    properties: List<PrettifiedProperty>,
    isLoading: Boolean,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(isLoading) {
        if (isLoading) {
            scrollState.animateScrollTo(0)
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
    ) {
        properties.forEachIndexed { index, property ->
            key(property.name) {
                PrettifiedPropertyView(
                    modifier = Modifier.fillMaxWidth(),
                    name = property.name,
                    value = property.value,
                    isLoading = isLoading,
                    isLast = index == properties.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun PrettifiedPropertyView(
    modifier: Modifier,
    name: String,
    value: String?,
    isLoading: Boolean,
    isLast: Boolean,
) {
    Box(
        modifier = modifier
            .copyOnLongPress(
                data = value.orEmpty(),
                enabled = !isLoading && value != null
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 24.dp,
                    top = 14.dp,
                    bottom = 14.dp,
                )
        ) {
            val adjustedValue = (value ?: "N/A")
            val valueColor = when (value) {
                null -> AppTheme.materialTheme.colorScheme.onSurfaceVariant
                else -> AppTheme.materialTheme.colorScheme.onBackground
            }


            val valueStyle = AppTheme.materialTheme.typography.bodyLarge
            val localDensity = LocalDensity.current
            val valuePaddingFromText = remember(localDensity) {
                with(localDensity) {
                    (valueStyle.lineHeight.toDp() - valueStyle.fontSize.toDp()) / 2
                }
            }
            Shimmable(
                modifier = Modifier
                    .padding(vertical = valuePaddingFromText),
                state = ShimmableState(
                    isShimmed = isLoading,
                    data = adjustedValue,
                ),
            ) { state ->
                Text(
                    color = valueColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.materialTheme.typography.bodyLarge.merge(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Proportional,
                            trim = LineHeightStyle.Trim.Both,
                        ),
                    ),
                    text = state.data,
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.materialTheme.typography.bodyMedium,
                text = name,
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = AppTheme.materialTheme.colorScheme.surfaceContainer,
            )
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    AppTheme {
        EventPrettifiedView(
            modifier = Modifier.fillMaxSize(),
            properties = StateMocks.prettifiedProperties,
            isLoading = false,
        )
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    AppTheme {
        EventPrettifiedView(
            modifier = Modifier.fillMaxSize(),
            properties = StateMocks.prettifiedProperties,
            isLoading = true,
        )
    }
}
