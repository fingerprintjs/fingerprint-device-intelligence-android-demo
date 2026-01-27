package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.Shimmable
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.ShimmableState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.TypographyExtended.textPrettifiedView
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.utils.ShowPreview

data class PrettifiedProperty(
    val name: String,
    val value: String,
    val isValueFaded: Boolean = false,
    val isValueItalic: Boolean = false,
    val onLongClick: () -> Unit = {},
    val onLongClickEnabled: Boolean = false,
    val isSmartSignal: Boolean = false,
    val onSmartSignalClick: () -> Unit = {},
)

@Composable
fun EventPrettifiedView(
    modifier: Modifier,
    properties: List<PrettifiedProperty>,
    isLoading: Boolean,
    isSmartSignalsLoading: Boolean
) {
    Column(modifier = modifier) {
        properties.forEachIndexed { index, property ->
            key(property.name) {
                PrettifiedPropertyView(
                    modifier = Modifier.fillMaxWidth(),
                    name = property.name,
                    value = property.value,
                    isValueFaded = property.isValueFaded,
                    isValueItalic = property.isValueItalic,
                    onLongClick = property.onLongClick,
                    onLongClickEnabled = property.onLongClickEnabled,
                    isSmartSignal = property.isSmartSignal,
                    onSmartSignalClick = property.onSmartSignalClick,
                    isLast = index == properties.lastIndex,
                    isLoading = if (property.isSmartSignal) {
                        isSmartSignalsLoading
                    } else {
                        isLoading
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrettifiedPropertyView(
    modifier: Modifier,
    name: String,
    value: String,
    isValueFaded: Boolean,
    isValueItalic: Boolean,
    onLongClick: () -> Unit,
    onLongClickEnabled: Boolean,
    isSmartSignal: Boolean,
    onSmartSignalClick: () -> Unit = {},
    isLoading: Boolean,
    isLast: Boolean,
) {
    Box(
        modifier = modifier
            .combinedClickable(
                enabled = onLongClickEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = onLongClick,
                onClick = {},
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
            val valueColor = when (isValueFaded) {
                true -> AppTheme.materialTheme.colorScheme.onSurfaceVariant
                false -> AppTheme.materialTheme.colorScheme.onBackground
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
                    data = value,
                ),
            ) { state ->
                Text(
                    color = valueColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = textPrettifiedView,
                    fontStyle = if (isValueItalic) FontStyle.Italic else FontStyle.Normal,
                    text = state.data,
                )
            }
            val smartSignalInlineImageAlternateText =
                "smart_signal_external_link"
            val smartSignalInlineImageId = "1"

            LinkableText(
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                links = if (!isSmartSignal) {
                    emptyList()
                } else {
                    listOf(
                        LinkableText.Link(
                            mask = "Smart Signal $smartSignalInlineImageAlternateText",
                            handler = onSmartSignalClick,
                        )
                    )
                },
                style = AppTheme.materialTheme.typography.bodyMedium,
                text = buildAnnotatedString {
                    if (!isSmartSignal) {
                        append(name)
                    } else {
                        append("$name - Smart Signal ")
                        appendInlineContent(
                            id = smartSignalInlineImageId,
                            alternateText = smartSignalInlineImageAlternateText,
                        )
                    }
                },
                inlineContent = mapOf(
                    smartSignalInlineImageId to InlineTextContent(
                        Placeholder(16.sp, 16.sp, PlaceholderVerticalAlign.TextCenter)
                    ) {
                        Image(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            colorFilter = ColorFilter.tint(AppTheme.materialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxSize(),
                            contentDescription = smartSignalInlineImageAlternateText,
                        )
                    }
                ),
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
    ShowPreview {
        with(HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked) {
            EventPrettifiedView(
                modifier = Modifier.fillMaxSize(),
                properties = prettifiedProps,
                isLoading = isLoading,
                isSmartSignalsLoading = isSmartSignalsLoading
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    ShowPreview {
        with(HomeScreenUiState.Content.LoadingOrSuccess.LoadingMocked) {
            EventPrettifiedView(
                modifier = Modifier.fillMaxSize(),
                properties = prettifiedProps,
                isLoading = isLoading,
                isSmartSignalsLoading = isSmartSignalsLoading
            )
        }
    }
}
