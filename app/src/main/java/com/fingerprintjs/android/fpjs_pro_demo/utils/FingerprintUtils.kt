package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.fingerprintjs.android.fpjs_pro.IpLocation

fun FingerprintJSProResponse.toJsonMap() = mapOf(
    "requestId" to requestId,
    "visitorId" to visitorId,
    "confidenceScore" to confidenceScore.run {
        mapOf(
            "score" to score
        )
    },
    "visitorFound" to visitorFound,
    "ipAddress" to ipAddress,
    // ✅ Only add ipLocation if it’s not null
    "ipLocation" to ipLocationData(ipLocation),
    "osName" to osName,
    "osVersion" to osVersion,
    "firstSeenAt" to firstSeenAt.run {
        mapOf(
            "global" to global,
            "subscription" to subscription,
        )
    },
    "lastSeenAt" to lastSeenAt.run {
        mapOf(
            "global" to global,
            "subscription" to subscription,
        )
    },
    "errorMessage" to errorMessage,
).filterValues { it != null }

private fun ipLocationData(ipLocation: IpLocation?): Map<String, Any>? {
    return ipLocation
        ?.takeIf { it.latitude != 0.0 && it.longitude != 0.0 }
        ?.let { loc ->
            mapOf(
                "accuracyRadius" to loc.accuracyRadius,
                "latitude" to loc.latitude,
                "longitude" to loc.longitude,
                "postalCode" to loc.postalCode,
                "timezone" to loc.timezone,
                "city" to mapOf("name" to loc.city.name),
                "country" to mapOf("name" to loc.country.name, "code" to loc.country.code),
                "continent" to mapOf("name" to loc.continent.name, "code" to loc.continent.code),
                "subdivisions" to loc.subdivisions.map { sub ->
                    mapOf("name" to sub.name, "isoCode" to sub.isoCode)
                }
            )
        }
}

val Configuration.Region.description: String
    get() = when (this) {
        Configuration.Region.US -> "Global (US)"
        Configuration.Region.EU -> "EU"
        Configuration.Region.AP -> "Asia (Mumbai)"
    }
