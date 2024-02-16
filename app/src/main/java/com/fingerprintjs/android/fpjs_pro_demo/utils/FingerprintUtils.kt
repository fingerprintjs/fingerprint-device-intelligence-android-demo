package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class FingerprintJSWrappedResult {
    class Success(val result: FingerprintJSProResponse) : FingerprintJSWrappedResult()
    class Error(val error: com.fingerprintjs.android.fpjs_pro.Error) : FingerprintJSWrappedResult()

    companion object {
        fun from(error: com.fingerprintjs.android.fpjs_pro.Error) = Error(error)
        fun from(result: FingerprintJSProResponse) = Success(result)
    }
}

suspend fun FingerprintJS.getVisitorId(): FingerprintJSWrappedResult {
    return suspendCancellableCoroutine { cancellableContinuation ->
        this.getVisitorId(
            listener = {
                cancellableContinuation.resume(
                    FingerprintJSWrappedResult.Success(it)
                )
            },
            errorListener = {
                cancellableContinuation.resume(
                    FingerprintJSWrappedResult.Error(it)
                )
            },
        )
    }
}

fun FingerprintJSProResponse.toPrettyJson(): String {
    val orderedMap = mapOf(
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
    )

    return GsonBuilder().setPrettyPrinting().create().toJson(orderedMap)
}
