package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
    AnimatedContent(
        modifier = modifier,
        targetState = state,
        transitionSpec = {
            val toShimmed = !initialState.isShimmed && targetState.isShimmed
            val fromShimmed = initialState.isShimmed && !targetState.isShimmed
            when {
                fromShimmed -> {
                    EnterTransition.None.togetherWith(fadeOut(animationSpec = tween())).apply {
                        this.targetContentZIndex = 0f
                    }
                }

                toShimmed -> {
                    fadeIn(animationSpec = tween()).togetherWith(
                        // this is a fake fadeout that serves as a "static" animation
                        fadeOut(animationSpec = tween(easing = { f -> f }))
                    ).apply {
                        this.targetContentZIndex = 1f
                    }
                }

                else -> {
                    // default transition
                    (
                        fadeIn(
                            animationSpec = tween(
                                220,
                                delayMillis = 90
                            )
                        ) + scaleIn(
                            initialScale = 0.92f,
                            animationSpec = tween(220, delayMillis = 90)
                        )
                        ).togetherWith(fadeOut(animationSpec = tween(90)))
                }
            }
        },
        label = "Shim animation",
    ) {
        if (it.isShimmed) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        rememberShimmer(
                            shimmerBounds = ShimmerBounds.View,
                            theme = ShimmerTheme(
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        800,
                                        easing = LinearEasing,
                                        delayMillis = 500,
                                    ),
                                    repeatMode = RepeatMode.Restart,
                                ),
                                blendMode = BlendMode.Src,
                                rotation = 15.0f,
                                shaderColors = listOf(
                                    AppTheme.materialTheme.colorScheme.surfaceContainer,
                                    AppTheme.materialTheme.colorScheme.surfaceContainerHighest,
                                    AppTheme.materialTheme.colorScheme.surfaceContainer,
                                ),
                                shaderColorStops = listOf(
                                    0.0f,
                                    0.5f,
                                    1.0f,
                                ),
                                shimmerWidth = 400.dp,
                            )
                        )
                    )
            ) {
                content(it)
            }
        } else {
            content(it)
        }
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
