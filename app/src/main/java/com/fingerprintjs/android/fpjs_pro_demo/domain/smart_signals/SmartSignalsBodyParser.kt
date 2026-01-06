package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartSignalsBodyParser @Inject constructor(
    val json: Json,
) {
    fun parseSmartSignals(body: String?): Result<SmartSignals, Unit> {
        val body = body.orEmpty()
        return runCatching {
            val rootObject = json.parseToJsonElement(body) as JsonObject
            val products = rootObject.get("products") as JsonObject
            SmartSignals(
                clonedApp = products.getSmartSignal(key = "clonedApp"),
                emulator = products.getSmartSignal(key = "emulator"),
                factoryReset = products.getSmartSignal(
                    key = "factoryReset",
                    validation = { !time.isEssentiallyEmpty() }
                ),
                frida = products.getSmartSignal(key = "frida"),
                ipInfo = products.getSmartSignal(key = "ipInfo"),
                highActivity = products.getSmartSignal(key = "highActivity"),
                ipBlocklist = products.getSmartSignal("ipBlocklist"),
                locationSpoofing = products.getSmartSignal(key = "locationSpoofing"),
                proxy = products.getSmartSignal("proxy"),
                root = products.getSmartSignal(key = "rootApps"),
                vpn = products.getSmartSignal(
                    key = "vpn",
                    transformation = {
                        this.copy(
                            originTimezone = originTimezone?.takeIfNotEssentiallyEmpty(),
                            originCountry = originCountry?.takeIfNotEssentiallyEmpty(),
                        )
                    }
                ),
                tampering = products.getSmartSignal(key = "tampering"),
                mitm = products.getSmartSignal(key = "mitmAttack"),
                proximity = products.getSmartSignal(
                    key = "proximity",
                    allowMissingData = true
                ),
            )
        }.mapError { }
    }

    fun parseSmartSignalsError(body: String?): Result<SmartSignalsError.APIError, Unit> {
        val body = body.orEmpty()
        return runCatching {
            json.decodeFromString<SmartSignalsErrorDto>(body).toSmartSignalsError()
        }.mapError { }
    }

    @VisibleForTesting
    @Suppress("ReturnCount")
    inline fun <reified T : SmartSignal> JsonObject.getSmartSignal(
        key: String,
        validation: T.() -> Boolean = { true },
        transformation: T.() -> T = { this },
        allowMissingData: Boolean = false,
    ): SmartSignalInfo<T> {
        val elem = get(key)
        if (elem == null) return SmartSignalInfo.Disabled(key)

        val parseError = SmartSignalInfo.ParseError(key, elem)

        val obj = elem as? JsonObject ?: return parseError
        if (obj.get("error") != null) return SmartSignalInfo.Error(key, elem)

        val dataObj = obj.get("data")?.let { it as? JsonObject }

        // If allowMissingData is true and data is missing, try to parse an empty object
        val dataToParse = if (dataObj == null && allowMissingData) {
            json.parseToJsonElement("{}") as JsonObject
        } else {
            dataObj ?: return parseError
        }

        val parsed = runCatching { json.decodeFromJsonElement<T>(dataToParse) }
            .getOr(null)?.takeIf { it.validation() } ?: return parseError
        return SmartSignalInfo.Success(
            rawKey = key,
            typedData = parsed.transformation(),
            rawData = elem
        )
    }

    private fun String.isEssentiallyEmpty(): Boolean =
        this.isEmpty() ||
            this == "n\\a" ||
            this.contentEquals("null", ignoreCase = true) ||
            this.contentEquals("unknown", ignoreCase = true)

    private fun String.takeIfNotEssentiallyEmpty(): String? = takeIf { !it.isEssentiallyEmpty() }

    private fun SmartSignalsErrorDto.toSmartSignalsError(): SmartSignalsError.APIError {
        return when (this.error.code) {
            "TokenRequired" -> SmartSignalsError.TokenRequired
            "TokenNotFound" -> SmartSignalsError.TokenNotFound
            "SubscriptionNotActive" -> SmartSignalsError.SubscriptionNotActive
            "WrongRegion" -> SmartSignalsError.WrongRegion
            "FeatureNotEnabled" -> SmartSignalsError.FeatureNotEnabled
            "RequestNotFound" -> SmartSignalsError.RequestNotFound
            else -> SmartSignalsError.UnknownApiError
        }
    }
}

@Serializable
private data class SmartSignalsErrorDto(
    val error: ErrorDto
)

@Serializable
private data class ErrorDto(
    val code: String? = null,
    val message: String? = null,
)
