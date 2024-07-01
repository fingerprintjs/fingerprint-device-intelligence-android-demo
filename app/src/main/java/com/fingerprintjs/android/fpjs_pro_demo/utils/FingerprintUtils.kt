package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse

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
    "ipLocation" to ipLocation?.run {
        mapOf(
            "accuracyRadius" to accuracyRadius,
            "latitude" to latitude,
            "longitude" to longitude,
            "postalCode" to postalCode,
            "timezone" to timezone,
            "city" to city.run {
                mapOf(
                    "name" to name
                )
            },
            "country" to country.run {
                mapOf(
                    "name" to name,
                    "code" to code,
                )
            },
            "continent" to continent.run {
                mapOf(
                    "name" to name,
                    "code" to code,
                )
            },
            "subdivisions" to subdivisions.map {
                mapOf(
                    "name" to it.name,
                    "isoCode" to it.isoCode,
                )
            }
        )
    },
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

val Configuration.Region.description: String
    get() = when(this) {
        Configuration.Region.US -> "Global (US)"
        Configuration.Region.EU -> "EU"
        Configuration.Region.AP -> "Asia (Mumbai)"
    }
