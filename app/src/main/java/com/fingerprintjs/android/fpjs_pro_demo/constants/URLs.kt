package com.fingerprintjs.android.fpjs_pro_demo.constants

object URLs {
    const val main = "https://fingerprint.com"
    const val support = "https://fingerprint.com/support"
    const val documentation = "https://dev.fingerprint.com/docs/android-sdk"
    val signup = Protected.signupUrl
    const val playStoreListing = "https://play.google.com/store/apps/details"
    const val privacyPolicy = "https://dev.fingerprint.com/docs/privacy-policy"

    object SmartSignalsOverview {
        val clonedApp = smartSignalOverviewUrl("cloned-app-detection")
        val emulator = smartSignalOverviewUrl("android-emulator-detection")
        val factoryReset = smartSignalOverviewUrl("factory-reset-detection")
        val frida = smartSignalOverviewUrl("frida-detection")
        val highActivity = smartSignalOverviewUrl("high-activity-device")
        val locationSpoofing = smartSignalOverviewUrl("geolocation-spoofing-detection")
        val root = smartSignalOverviewUrl("rooted-device-detection")
        val vpn = smartSignalOverviewUrl("vpn-detection-for-mobile-devices")

        private fun smartSignalOverviewUrl(forAnchor: String): String {
            return "https://dev.fingerprint.com/docs/smart-signals-overview#$forAnchor"
        }
    }
}
