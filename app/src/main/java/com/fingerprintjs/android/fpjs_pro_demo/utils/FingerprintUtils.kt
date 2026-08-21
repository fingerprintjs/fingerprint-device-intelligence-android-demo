package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprint.android.Configuration
import com.fingerprint.android.FingerprintResponse

fun FingerprintResponse.toJsonMap(): Map<String, Any?> = mapOf<String, Any?>(
    "eventId" to eventId,
    "visitorId" to visitorId,
    "suspectScore" to suspectScore,
    "errorMessage" to errorMessage,
).filterValues { it != null }

val Configuration.Region.description: String
    get() = when (this) {
        Configuration.Region.US -> "Global (US)"
        Configuration.Region.EU -> "EU"
        Configuration.Region.AP -> "Asia (Mumbai)"
    }
