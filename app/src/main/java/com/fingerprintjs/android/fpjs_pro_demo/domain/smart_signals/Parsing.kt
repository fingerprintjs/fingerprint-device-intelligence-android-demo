package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.runCatching


fun SmartSignalsDto.toSmartSignals(): SmartSignals {
    fun <I1, I2 : SmartSignalsDto.SmartSignalEntryDto<I1>, O : SmartSignal> I2?.toSignal(mapper: I2.() -> O): O? {
        return this?.let {
            runCatching {
                it.mapper()
            }.getOr(null)
        }
    }

    fun String.dropEssentiallyEmpty(): String? = takeIf {
        it.isNotEmpty()
                && it != "n\\a"
                && !it.contentEquals("null", ignoreCase = true)
                && !it.contentEquals("unknown", ignoreCase = true)
    }

    return SmartSignals(
        clonedApp = products.clonedApp.toSignal {
            SmartSignal.ClonedApp(
                detected = data!!.result!!,
                rawObject = this,
                rawKey = SmartSignalsDto.CLONED_APP_KEY,
            )
        },
        emulator = products.emulator.toSignal {
            SmartSignal.Emulator(
                detected = data!!.result!!,
                rawObject = this,
                rawKey = SmartSignalsDto.EMULATOR_KEY,
            )
        },
        factoryReset = products.factoryReset.toSignal {
            SmartSignal.FactoryReset(
                time = data!!.time!!.dropEssentiallyEmpty()!!,
                timestamp = data.timestamp!!,
                rawObject = this,
                rawKey = SmartSignalsDto.FACTORY_RESET_KEY,
            )
        },
        frida = products.frida.toSignal {
            SmartSignal.Frida(
                detected = data!!.result!!,
                rawObject = this,
                rawKey = SmartSignalsDto.FRIDA_KEY,
            )
        },
        highActivity = products.highActivity.toSignal {
            SmartSignal.HighActivity(
                detected = data!!.result!!,
                dailyRequests = data.dailyRequests,
                rawObject = this,
                rawKey = SmartSignalsDto.HIGH_ACTIVITY_KEY,
            )
        },
        locationSpoofing = products.locationSpoofing.toSignal {
            SmartSignal.LocationSpoofing(
                detected = data!!.result!!,
                rawObject = this,
                rawKey = SmartSignalsDto.LOCATION_SPOOFING_KEY,
            )
        },
        root = products.root.toSignal {
            SmartSignal.Root(
                detected = data!!.result!!,
                rawObject = this,
                rawKey = SmartSignalsDto.ROOT_KEY,
            )
        },
        vpn = products.vpn.toSignal {
            SmartSignal.Vpn(
                detected = data!!.result!!,
                originTimezone = data.originTimezone?.dropEssentiallyEmpty(),
                originCountry = data.originCountry?.dropEssentiallyEmpty(),
                timezoneMismatch = data.methods!!.timezoneMismatch!!,
                publicVPN = data.methods.publicVPN!!,
                auxiliaryMobile = data.methods.auxiliaryMobile!!,
                osMismatch = data.methods.osMismatch!!,
                rawObject = this,
                rawKey = SmartSignalsDto.VPN_KEY,
            )
        },
    )
}

fun SmartSignalsErrorDto.toSmartSignalsError(): SmartSignalsError.APIError {
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
