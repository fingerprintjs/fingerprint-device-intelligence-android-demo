package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.fingerprintjs.android.fpjs_pro_demo.network.SmartSignalsApi

class SmartSignals(
    val clonedApp: SmartSignal.ClonedApp?,
    val emulator: SmartSignal.Emulator?,
    val factoryReset: SmartSignal.FactoryReset?,
    val frida: SmartSignal.Frida?,
    val highActivity: SmartSignal.HighActivity?,
    val locationSpoofing: SmartSignal.LocationSpoofing?,
    val root: SmartSignal.Root?,
    val vpn: SmartSignal.Vpn?,
) {
    companion object {
        val EMPTY = SmartSignals(null, null, null, null, null, null, null, null)
    }
}

sealed class SmartSignal(
    val rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
    val rawKey: String,
) {
    class ClonedApp(
        val detected: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class Emulator(
        val detected: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class FactoryReset(
        val time: String,
        val timestamp: Int,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class Frida(
        val detected: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class HighActivity(
        val detected: Boolean,
        val dailyRequests: Int?,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class LocationSpoofing(
        val detected: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class Root(
        val detected: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)

    class Vpn(
        val detected: Boolean,
        val originTimezone: String?,
        val originCountry: String?,
        val timezoneMismatch: Boolean,
        val publicVPN: Boolean,
        val auxiliaryMobile: Boolean,
        val osMismatch: Boolean,
        rawObject: SmartSignalsApi.SmartSignalEntryDto<*>,
        rawKey: String,
    ) : SmartSignal(rawObject, rawKey)
}
