package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.fingerprintjs.android.fpjs_pro_demo.R
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModelState

@Stable
data class HomeErrorScreenState(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String,
    val links: List<LinkableText.Link>,
    val onButtonCLick: () -> Unit,
) {

    companion object
}

fun HomeErrorScreenState.Companion.from(
    state: HomeViewModelState.Error,
    onSupport: () -> Unit,
    onReload: () -> Unit,
): HomeErrorScreenState = when (state) {
    HomeViewModelState.Error.Network -> HomeErrorScreenState(
        image = R.drawable.ic_cloud_crossed,
        title = "Server cannot be reached",
        description = "Please check your network settings and try again.",
        links = emptyList(),
        onButtonCLick = onReload
    )

    HomeViewModelState.Error.TooManyRequests -> HomeErrorScreenState(
        image = R.drawable.ic_hand_raised,
        title = "Too many requests",
        description = " The request rate limit set for the public API key was exceeded.",
        links = emptyList(),
        onButtonCLick = onReload
    )

    HomeViewModelState.Error.Unknown -> HomeErrorScreenState(
        image = R.drawable.ic_exclamation_in_circle,
        title = "An unexpected error occurred...",
        description = " Please contact support if this issue persists.",
        links = listOf(
            LinkableText.Link(
                mask = "contact support",
                handler = onSupport,
            )
        ),
        onButtonCLick = onReload
    )
}
