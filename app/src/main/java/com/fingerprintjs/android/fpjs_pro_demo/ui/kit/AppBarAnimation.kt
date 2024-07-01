package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.animatedAppBar(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
): Modifier {
    return if (sharedTransitionScope != null && animatedContentScope != null) {
        with(sharedTransitionScope) {
            this@animatedAppBar.sharedBounds(
                sharedContentState = sharedTransitionScope.rememberSharedContentState(
                    key = SHARED_APP_BAR_KEY,
                ),
                animatedVisibilityScope = animatedContentScope,
            )
        }
    } else {
        this
    }
}

private const val SHARED_APP_BAR_KEY = "appbar"