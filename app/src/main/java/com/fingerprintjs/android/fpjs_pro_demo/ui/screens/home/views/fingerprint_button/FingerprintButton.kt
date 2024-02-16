package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.fingerprint_button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.R
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun FingerprintButton(
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    val scale by updateButtonScaleAnimation(isButtonPressed)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AppTheme.extendedTheme.colorScheme.primaryGradientStart,
                        AppTheme.extendedTheme.colorScheme.primaryGradientStart,
                        AppTheme.extendedTheme.colorScheme.primaryGradientEnd,
                    ),
                ),
            )
            .clickable(
                interactionSource = buttonInteractionSource,
                indication = LocalIndication.current,
            ) { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            Spacer(modifier = Modifier.weight(0.22f))
            Image(
                modifier = Modifier
                    .weight(0.56f)
                    .fillMaxSize(),
                painter = painterResource(R.drawable.ic_fingerprint),
                contentDescription = "Fingerprint button",
                colorFilter = ColorFilter.tint(color = AppTheme.materialTheme.colorScheme.surface)
            )
            Spacer(modifier = Modifier.weight(0.22f))
        }
    }

}

@Composable
private fun updateButtonScaleAnimation(isPressed: Boolean): State<Float> {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        launch {
            if (isPressed) {
                scale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(durationMillis = 250, easing = EaseOut)
                )
            } else {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600, easing = EaseOut)
                )
                scale.animateTo(
                    targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 600, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse,
                    )
                )
            }
        }
    }
    return scale.asState()
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppTheme {
        FingerprintButton(
            onClick = {},
            modifier = Modifier.size(136.dp),
        )
    }
}
