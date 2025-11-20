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
    
    // Visitor found values
    const val YES = "Yes"
    const val NO = "No"
    
    // VPN detection messages
    const val DEVICE_HAS_VPN_ENABLED = "Device has VPN enabled"
    const val DEVICE_LOCATION_IS = "Device location is"
    const val DEVICE_TIMEZONE_IS = "Device timezone is"
    
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
}