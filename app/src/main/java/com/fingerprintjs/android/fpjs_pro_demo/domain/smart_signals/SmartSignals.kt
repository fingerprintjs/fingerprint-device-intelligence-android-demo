package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Suppress("LongParameterList")
class SmartSignals(
    val clonedApp: SmartSignalInfo<SmartSignal.ClonedApp>,
    val emulator: SmartSignalInfo<SmartSignal.Emulator>,
    val factoryReset: SmartSignalInfo<SmartSignal.FactoryReset>,
    val frida: SmartSignalInfo<SmartSignal.Frida>,
    val highActivity: SmartSignalInfo<SmartSignal.HighActivity>,
    val locationSpoofing: SmartSignalInfo<SmartSignal.LocationSpoofing>,
    val root: SmartSignalInfo<SmartSignal.Root>,
    val vpn: SmartSignalInfo<SmartSignal.Vpn>,
    val tampering: SmartSignalInfo<SmartSignal.Tampering>,
    val mitm: SmartSignalInfo<SmartSignal.Mitm>,
    val ipBlocklist: SmartSignalInfo<SmartSignal.IPBlocklist>,
    val proxy: SmartSignalInfo<SmartSignal.Proxy>,
    val ipInfo: SmartSignalInfo<SmartSignal.IPInfo>
)

sealed class SmartSignalInfo<out T : SmartSignal>(val rawKey: String) {

    sealed interface WithRawData {
        val rawData: JsonElement
    }

    class Success<T : SmartSignal>(
        rawKey: String,
        val typedData: T,
        override val rawData: JsonElement
    ) : SmartSignalInfo<T>(rawKey), WithRawData

    class Error(
        rawKey: String,
        override val rawData: JsonElement
    ) : SmartSignalInfo<Nothing>(rawKey), WithRawData

    class ParseError(
        rawKey: String,
        override val rawData: JsonElement
    ) : SmartSignalInfo<Nothing>(rawKey), WithRawData

    class Disabled(rawKey: String) : SmartSignalInfo<Nothing>(rawKey)
}

sealed class SmartSignal {
    @Serializable
    data class ClonedApp(
        val result: Boolean
    ) : SmartSignal()

    @Serializable
    data class Emulator(
        val result: Boolean
    ) : SmartSignal()

    @Serializable
    data class FactoryReset(
        val time: String,
        val timestamp: Long,
    ) : SmartSignal()

    @Serializable
    data class Frida(
        val result: Boolean,
    ) : SmartSignal()

    @Serializable
    data class HighActivity(
        val result: Boolean,
        val dailyRequests: Int? = null,
    ) : SmartSignal()

    @Serializable
    data class LocationSpoofing(
        val result: Boolean,
    ) : SmartSignal()

    @Serializable
    data class Root(
        val result: Boolean,
    ) : SmartSignal()

    @Serializable
    data class Vpn(
        val result: Boolean,
        val originTimezone: String? = null,
        val originCountry: String? = null,
        val methods: Map<String, Boolean> = mapOf(),
        val confidence: String? = null
    ) : SmartSignal()

    @Serializable
    data class Tampering(
        val result: Boolean,
        val anomalyScore: Float,
    ) : SmartSignal()

    @Serializable
    data class Mitm(
        val result: Boolean,
    ) : SmartSignal()

    @Serializable
    data class ASN(
        val asn: String,
        val name: String,
        val network: String,
    ) : SmartSignal()

    @Serializable
    data class DataCenter(
        val result: Boolean,
        val name: String,
    ) : SmartSignal()

    @Serializable
    data class IPBlocklist(
        val result: Boolean,
        val details: Map<String, Boolean> = mapOf(),
    ) : SmartSignal()

    @Serializable
    data class Proxy(
        val result: Boolean,
        val confidence: String,
        val details: Map<String, String> = mapOf(),
    ) : SmartSignal()

    @Serializable
    data class IPInfo(
        val v4: IPV4,
    ) : SmartSignal()

    @Serializable
    data class GeoLocation(
        val accuracyRadius: Int,
        val latitude: Double,
        val longitude: Double,
        val postalCode: Int,
        val timezone: String,
        val city: City,
        val country: Country,
        val continent: Continent,
    ) : SmartSignal()

    @Serializable
    data class City(
        val name: String,
    )

    @Serializable
    data class Country(
        val code: String,
        val name: String,
    )

    @Serializable
    data class Continent(
        val code: String,
        val name: String,
    )

    @Serializable
    data class IPV4(
        val address: String,
        val geolocation: GeoLocation,
        val asn: ASN,
        val datacenter: DataCenter,
    ) : SmartSignal()
}
