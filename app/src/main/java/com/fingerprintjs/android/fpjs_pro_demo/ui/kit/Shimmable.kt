package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Stable
data class ShimmableState<T>(
    val isShimmed: Boolean,
    val data: T,
)

@Composable
fun <T> Shimmable(
    state: ShimmableState<T>,
    modifier: Modifier = Modifier,
    content: @Composable (ShimmableState<T>) -> Unit,
) {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.View,
        theme = ShimmerTheme(
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 800,
                    easing = LinearEasing,
                    delayMillis = 500,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            // DstIn is the library default; Src + AnimatedContent previously stacked
            // graphics-layer work during loading and matched prod SIGSEGV draw stacks.
            blendMode = BlendMode.DstIn,
            rotation = 15.0f,
            shaderColors = listOf(
                AppTheme.materialTheme.colorScheme.surfaceContainer.copy(alpha = 0.25f),
                AppTheme.materialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 1.00f),
                AppTheme.materialTheme.colorScheme.surfaceContainer.copy(alpha = 0.25f),
            ),
            shaderColorStops = listOf(
                0.0f,
                0.5f,
                1.0f,
            ),
            shimmerWidth = 400.dp,
        ),
    )

    Box(
        modifier = modifier
            .wrapContentSize()
            .then(
                if (state.isShimmed) {
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer(shimmer)
                } else {
                    Modifier
                },
            ),
    ) {
        content(state)
    }
}

@PreviewLightDark
@Composable
private fun ShimmedText() {
    AppTheme {
        Column {
            var shimEnabled by remember { mutableStateOf(true) }
            val text = remember(shimEnabled) {
                if (shimEnabled) "Shim enabled" else "Shim disabled"
            }
            Shimmable(
                state = ShimmableState(isShimmed = shimEnabled, data = text),
            ) {
                Text(
                    text = it.data,
                    maxLines = 1,
                )
            }

            Button(
                onClick = { shimEnabled = !shimEnabled },
            ) {
                Text("Toggle shim")
            }
        }
    }
}
