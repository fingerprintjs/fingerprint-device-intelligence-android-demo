package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooManyFunctions", "ReturnCount")
class SmartSignalsBodyParser @Inject constructor(
    val json: Json,
) {
    fun parseSmartSignals(body: String?): Result<SmartSignals, Unit> {
        val raw = body.orEmpty()
        return runCatching {
            val root = json.parseToJsonElement(raw) as JsonObject
            SmartSignals(
                clonedApp = root.flatBoolSignal("cloned_app", rawKey = "clonedApp") {
                    SmartSignal.ClonedApp(it)
                },
                emulator = root.flatBoolSignal("emulator", rawKey = "emulator") {
                    SmartSignal.Emulator(it)
                },
                factoryReset = root.parseFactoryReset(),
                frida = root.flatBoolSignal("frida", rawKey = "frida") {
                    SmartSignal.Frida(it)
                },
                highActivity = root.flatBoolSignal("high_activity_device", rawKey = "highActivity") {
                    SmartSignal.HighActivity(result = it, dailyRequests = null)
                },
                ipInfo = root.parseIpInfo(),
                ipBlocklist = root.parseIpBlocklist(),
                locationSpoofing = root.flatBoolSignal("location_spoofing", rawKey = "locationSpoofing") {
                    SmartSignal.LocationSpoofing(it)
                },
                proxy = root.parseProxy(),
                root = root.flatBoolSignal("root_apps", rawKey = "rootApps") {
                    SmartSignal.Root(it)
                },
                vpn = root.parseVpn(),
                tampering = root.parseTampering(),
                mitm = root.flatBoolSignal("mitm_attack", rawKey = "mitmAttack") {
                    SmartSignal.Mitm(it)
                },
                proximity = root.parseProximity(),
                developerTools = root.flatBoolSignal("developer_tools", rawKey = "developerTools") {
                    SmartSignal.DeveloperTools(it)
                },
                identificationInfo = root.parseIdentificationInfo(),
            )
        }.mapError { }
    }

    fun parseSmartSignalsError(body: String?): Result<SmartSignalsError.APIError, Unit> {
        val raw = body.orEmpty()
        return runCatching {
            json.decodeFromString<SmartSignalsErrorDto>(raw).toSmartSignalsError()
        }.mapError { }
    }

    private inline fun <T : SmartSignal> JsonObject.flatBoolSignal(
        v4Key: String,
        rawKey: String,
        factory: (Boolean) -> T,
    ): SmartSignalInfo<T> {
        val elem = get(v4Key) ?: return SmartSignalInfo.Disabled(rawKey)
        val bool = (elem as? JsonPrimitive)?.booleanOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, elem)
        return SmartSignalInfo.Success(rawKey, factory(bool), elem)
    }

    private fun JsonObject.parseFactoryReset(): SmartSignalInfo<SmartSignal.FactoryReset> {
        val rawKey = "factoryReset"
        val elem = get("factory_reset_timestamp") ?: return SmartSignalInfo.Disabled(rawKey)
        val timestamp = (elem as? JsonPrimitive)?.longOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, elem)
        val time = Instant.ofEpochSecond(timestamp).toString()
        val rawData = buildJsonObject {
            put("time", time)
            put("timestamp", timestamp)
        }
        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.FactoryReset(time = time, timestamp = timestamp),
            rawData = rawData,
        )
    }

    private fun JsonObject.parseVpn(): SmartSignalInfo<SmartSignal.Vpn> {
        val rawKey = "vpn"
        val resultElem = get("vpn") ?: return SmartSignalInfo.Disabled(rawKey)
        val result = (resultElem as? JsonPrimitive)?.booleanOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, resultElem)
        val confidence = (get("vpn_confidence") as? JsonPrimitive)?.contentOrNull
        val originTimezone = (get("vpn_origin_timezone") as? JsonPrimitive)?.contentOrNull
            ?.takeIfNotEssentiallyEmpty()
        val originCountry = (get("vpn_origin_country") as? JsonPrimitive)?.contentOrNull
            ?.takeIfNotEssentiallyEmpty()
        val methodsObj = get("vpn_methods") as? JsonObject
        val methods = methodsObj?.mapNotNull { (key, value) ->
            (value as? JsonPrimitive)?.booleanOrNull?.let { key to it }
        }?.toMap() ?: emptyMap()

        val rawData = buildJsonObject {
            put("vpn", result)
            confidence?.let { put("vpn_confidence", it) }
            originTimezone?.let { put("vpn_origin_timezone", it) }
            originCountry?.let { put("vpn_origin_country", it) }
            methodsObj?.let { put("vpn_methods", it) }
        }

        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.Vpn(
                result = result,
                originTimezone = originTimezone,
                originCountry = originCountry,
                methods = methods,
                confidence = confidence,
            ),
            rawData = rawData,
        )
    }

    private fun JsonObject.parseTampering(): SmartSignalInfo<SmartSignal.Tampering> {
        val rawKey = "tampering"
        val resultElem = get("tampering") ?: return SmartSignalInfo.Disabled(rawKey)
        val result = (resultElem as? JsonPrimitive)?.booleanOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, resultElem)
        val detailsObj = get("tampering_details") as? JsonObject
        val anomalyScore = (detailsObj?.get("anomaly_score") as? JsonPrimitive)?.floatOrNull
            ?: 0f

        val rawData = buildJsonObject {
            put("tampering", result)
            detailsObj?.let { put("tampering_details", it) }
        }

        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.Tampering(result = result, anomalyScore = anomalyScore),
            rawData = rawData,
        )
    }

    private fun JsonObject.parseProxy(): SmartSignalInfo<SmartSignal.Proxy> {
        val rawKey = "proxy"
        val resultElem = get("proxy") ?: return SmartSignalInfo.Disabled(rawKey)
        val result = (resultElem as? JsonPrimitive)?.booleanOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, resultElem)
        val confidence = (get("proxy_confidence") as? JsonPrimitive)?.contentOrNull.orEmpty()
        val detailsObj = get("proxy_details") as? JsonObject
        val proxyType = (detailsObj?.get("proxy_type") as? JsonPrimitive)?.contentOrNull
        val lastSeenAt = (detailsObj?.get("last_seen_at") as? JsonPrimitive)?.let { prim ->
            prim.longOrNull?.let { Instant.ofEpochMilli(it).toString() } ?: prim.contentOrNull
        }
        val details = buildMap {
            proxyType?.let { put("proxy_type", it) }
            lastSeenAt?.let { put("last_seen_at", it) }
        }

        val rawData = buildJsonObject {
            put("proxy", result)
            put("proxy_confidence", confidence)
            detailsObj?.let { put("proxy_details", it) }
        }

        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.Proxy(result = result, confidence = confidence, details = details),
            rawData = rawData,
        )
    }

    private fun JsonObject.parseIpBlocklist(): SmartSignalInfo<SmartSignal.IPBlocklist> {
        val rawKey = "ipBlocklist"
        val obj = get("ip_blocklist") as? JsonObject ?: return SmartSignalInfo.Disabled(rawKey)
        val emailSpam = (obj["email_spam"] as? JsonPrimitive)?.booleanOrNull == true
        val attackSource = (obj["attack_source"] as? JsonPrimitive)?.booleanOrNull == true
        val torNode = (obj["tor_node"] as? JsonPrimitive)?.booleanOrNull == true
        val result = emailSpam || attackSource || torNode
        val details = mapOf(
            "email_spam" to emailSpam,
            "attack_source" to attackSource,
        )
        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.IPBlocklist(result = result, details = details),
            rawData = obj,
        )
    }

    private fun JsonObject.parseIpInfo(): SmartSignalInfo<SmartSignal.IPInfo> {
        val rawKey = "ipInfo"
        val ipInfoObj = get("ip_info") as? JsonObject ?: return SmartSignalInfo.Disabled(rawKey)
        val v4Obj = ipInfoObj["v4"] as? JsonObject ?: return SmartSignalInfo.Disabled(rawKey)
        val address = (v4Obj["address"] as? JsonPrimitive)?.contentOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, ipInfoObj)
        val geoObj = v4Obj["geolocation"] as? JsonObject
            ?: return SmartSignalInfo.ParseError(rawKey, ipInfoObj)
        val geo = parseGeoLocation(geoObj)
            ?: return SmartSignalInfo.ParseError(rawKey, ipInfoObj)
        val asnName = (v4Obj["asn"] as? JsonPrimitive)?.contentOrNull
            ?: return SmartSignalInfo.ParseError(rawKey, ipInfoObj)
        val asn = SmartSignal.ASN(
            asn = asnName,
            name = (v4Obj["asn_name"] as? JsonPrimitive)?.contentOrNull.orEmpty(),
            network = (v4Obj["asn_network"] as? JsonPrimitive)?.contentOrNull.orEmpty(),
        )
        val datacenter = SmartSignal.DataCenter(
            result = (v4Obj["datacenter_result"] as? JsonPrimitive)?.booleanOrNull == true,
            name = "",
        )
        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.IPInfo(
                v4 = SmartSignal.IPV4(
                    address = address,
                    geolocation = geo,
                    asn = asn,
                    datacenter = datacenter,
                )
            ),
            rawData = ipInfoObj,
        )
    }

    private fun JsonObject.parseIdentificationInfo(): SmartSignalInfo<SmartSignal.IdentificationInfo> {
        val rawKey = "identificationInfo"
        val identObj = get("identification") as? JsonObject ?: return SmartSignalInfo.Disabled(rawKey)
        val visitorFound = (identObj["visitor_found"] as? JsonPrimitive)?.booleanOrNull
            ?: return SmartSignalInfo.Disabled(rawKey)
        val confidenceScore = (identObj["confidence"] as? JsonObject)
            ?.let { (it["score"] as? JsonPrimitive)?.doubleOrNull } ?: 0.0
        val firstSeenAt = (identObj["first_seen_at"] as? JsonPrimitive)?.longOrNull ?: 0L
        val lastSeenAt = (identObj["last_seen_at"] as? JsonPrimitive)?.longOrNull ?: 0L
        val rawData = buildJsonObject {
            put("visitor_found", visitorFound)
            put("confidence_score", confidenceScore)
            put("first_seen_at", firstSeenAt)
            put("last_seen_at", lastSeenAt)
        }
        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.IdentificationInfo(
                visitorFound = visitorFound,
                confidenceScore = confidenceScore,
                firstSeenAt = firstSeenAt,
                lastSeenAt = lastSeenAt,
            ),
            rawData = rawData,
        )
    }

    private fun parseGeoLocation(obj: JsonObject): SmartSignal.GeoLocation? {
        val accuracy = (obj["accuracy_radius"] as? JsonPrimitive)?.intOrNull ?: return null
        val latitude = (obj["latitude"] as? JsonPrimitive)?.doubleOrNull ?: return null
        val longitude = (obj["longitude"] as? JsonPrimitive)?.doubleOrNull ?: return null
        val postalCode = (obj["postal_code"] as? JsonPrimitive)?.let { prim ->
            prim.intOrNull ?: prim.contentOrNull?.toIntOrNull()
        } ?: return null
        val timezone = (obj["timezone"] as? JsonPrimitive)?.contentOrNull ?: return null
        val cityName = (obj["city_name"] as? JsonPrimitive)?.contentOrNull ?: return null
        val countryCode = (obj["country_code"] as? JsonPrimitive)?.contentOrNull ?: return null
        val countryName = (obj["country_name"] as? JsonPrimitive)?.contentOrNull ?: return null
        val continentCode = (obj["continent_code"] as? JsonPrimitive)?.contentOrNull ?: return null
        val continentName = (obj["continent_name"] as? JsonPrimitive)?.contentOrNull ?: return null
        return SmartSignal.GeoLocation(
            accuracyRadius = accuracy,
            latitude = latitude,
            longitude = longitude,
            postalCode = postalCode,
            timezone = timezone,
            city = SmartSignal.City(name = cityName),
            country = SmartSignal.Country(code = countryCode, name = countryName),
            continent = SmartSignal.Continent(code = continentCode, name = continentName),
        )
    }

    private fun JsonObject.parseProximity(): SmartSignalInfo<SmartSignal.Proximity> {
        val rawKey = "proximity"
        val obj = get("proximity") as? JsonObject ?: return SmartSignalInfo.Disabled(rawKey)
        return SmartSignalInfo.Success(
            rawKey = rawKey,
            typedData = SmartSignal.Proximity(
                id = (obj["id"] as? JsonPrimitive)?.contentOrNull,
                precisionRadius = (obj["precision_radius"] as? JsonPrimitive)?.intOrNull,
                confidence = (obj["confidence"] as? JsonPrimitive)?.floatOrNull,
            ),
            rawData = obj,
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
