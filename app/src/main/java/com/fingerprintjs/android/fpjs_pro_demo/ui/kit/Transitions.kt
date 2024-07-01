package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Alignment

fun verticalExpandTransition(): EnterTransition = expandVertically(
    expandFrom = Alignment.Top,
    animationSpec = tween(),
) + fadeIn(
    animationSpec = tween(delayMillis = 100)
)

fun verticalShrinkTransition(): ExitTransition =
    shrinkVertically(
        shrinkTowards = Alignment.Top,
        animationSpec = tween(delayMillis = 100)
    ) + fadeOut(
        animationSpec = tween()
    )
