package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
        val methods: Map<String, Boolean> = mapOf()
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
}
