package com.fingerprintjs.android.fpjs_pro_demo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.fingerprintjs.android.fpjs_pro_demo.R

enum class Tab(
    val route: String,
    val title: String,
    val icon: Icon,
) {
    Home(
        route = "home",
        title = "Home",
        icon = Icon.Vector(Icons.Outlined.Home),
    ),
    DRN(
        route = "drn",
        title = "DRN",
        icon = Icon.Resource(R.drawable.drn),
    ),
    Settings(
        "settings",
        title = "Settings",
        icon = Icon.Vector(Icons.Outlined.Settings),
    );

    sealed class Icon {
        data class Vector(
            val value: ImageVector
        ) : Icon()

        data class Resource(
            @DrawableRes val resId: Int
        ) : Icon()
    }
}
