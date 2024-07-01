package com.fingerprintjs.android.fpjs_pro_demo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Tab(
    val route: String,
    val title: String,
    val icon: ImageVector,
    ) {
    Home(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home,
    ),
    Settings(
        "settings",
        title = "Settings",
        icon = Icons.Outlined.Settings,
    ),
}