package com.fingerprintjs.android.fpjs_pro_demo.constants

object StringConstants {
    // Detection status strings
    const val NOT_DETECTED = "Not detected"
    const val DETECTED = "Detected"
    const val NOT_AVAILABLE = "N/A"

    // Signal disabled message
    const val SIGNAL_DISABLED = "Signal disabled for your account"

    // Property names
    const val REQUEST_ID = "Request ID"
    const val VISITOR_ID = "Visitor ID"
    const val VISITOR_FOUND = "Visitor Found"
    const val CONFIDENCE = "Confidence"
    const val IP_ADDRESS = "IP Address"
    const val FIRST_SEEN_AT = "First Seen At"
    const val LAST_SEEN_AT = "Last Seen At"

    // Smart signal names
    const val CLONED_APP = "Cloned App"
    const val EMULATOR = "Emulator"
    const val FACTORY_RESET = "Factory Reset"
    const val FRIDA = "Frida"
    const val HIGH_ACTIVITY = "High Activity"
    const val IP_BLOCKLIST_MATCH = "IP Blocklist Match"
    const val IP_LOCATION = "IP Location"
    const val IP_NETWORK_PROVIDER = "IP Network Provider"
    const val GEOLOCATION_SPOOFING = "Geolocation Spoofing"
    const val MITM_ATTACK = "MITM Attack"
    const val PROXY = "Proxy"
    const val ROOTED_DEVICE = "Rooted Device"
    const val TAMPERED_REQUEST = "Tampered Request"
    const val VPN = "VPN"
    const val PROXIMITY = "Proximity Detection"

    // Smart signal link text
    const val SMART_SIGNAL = "Smart Signal"
    const val MORE_INFO = "More Info"

    // Proximity value formatting
    const val PROXIMITY_ID_PREFIX = "Proximity ID: "
    const val PROXIMITY_PRECISION_RADIUS_PREFIX = "Precision Radius: "
    const val PROXIMITY_PRECISION_RADIUS_SUFFIX = "m"
    const val PROXIMITY_CONFIDENCE_PREFIX = "Confidence: "
    const val PROXIMITY_REQUIRES_PERMISSION = "Requires location permission"
    const val PROXIMITY_DOC_URL =
        "https://dev.fingerprint.com/docs/native-android-integration#proximity-detection-for-android-devices"

    // Visitor found values
    const val YES = "Yes"
    const val NO = "No"

    // High activity message
    const val PER_DAY = "per day"

    // JSON keys
    const val IDENTIFICATION = "identification"
    const val SMART_SIGNALS = "smartSignals"

    // Empty value checks
    const val N_A_ESCAPED = "n\\a"
    const val NULL_STRING = "null"

    // Format strings
    const val PERCENTAGE = "%"

    // Relative time strings
    const val JUST_NOW = "Just now"
    const val AGO = "ago"
    const val MINUTE = "minute"
    const val MINUTES = "minutes"
    const val HOUR = "hour"
    const val HOURS = "hours"
    const val DAY = "day"
    const val DAYS = "days"
    const val WEEK = "week"
    const val WEEKS = "weeks"
    const val VPN_NOTE_STRING = "Note: works without location permissions"

    // VPN Detection
    val VPN_METHOD_LABELS = mapOf(
        "publicVPN" to "Public VPN",
        "timezoneMismatch" to "Timezone mismatch",
        "relay" to "Relay",
        "auxiliaryMobile" to "Auxiliary mobile",
    )

    val VPN_METHOD_PRIORITY = listOf(
        "publicVPN",
        "timezoneMismatch",
        "relay",
        "auxiliaryMobile",
    )

    const val COUNTRY_CODE_LENGTH = 2
    const val ASCII_UPPERCASE_A_CODEPOINT = 0x41
    const val REGIONAL_INDICATOR_BASE = 0x1F1E6
}
