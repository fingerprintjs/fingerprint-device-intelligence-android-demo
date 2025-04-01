package com.fingerprintjs.android.fpjs_pro_demo.ui.navigation

enum class Screen(
    val tab: Tab,
    private val route: String,
    val withNavBar: Boolean,
) {
    Home(Tab.Home, "main", true),
    DRN(Tab.DRN, "main", true),
    Settings(Tab.Settings, "main", true),
    SettingsDetails(Tab.Settings, "details", false);

    val absoluteRoute: String
        get() = "${tab.route}/$route"

    companion object {
        fun from(route: String?): Screen? {
            return entries.firstOrNull {
                it.absoluteRoute == route
            }
        }
    }
}
