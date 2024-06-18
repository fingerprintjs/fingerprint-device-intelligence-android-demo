package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.fingerprintjs.android.fpjs_pro_demo.constants.Protected
import com.fingerprintjs.android.fpjs_pro_demo.network.SmartSignalsApi
import com.fingerprintjs.android.fpjs_pro_demo.utils.runCatchingCancellable
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

typealias SmartSignalsResponse = Result<SmartSignals, SmartSignalsProvider.Error>

class SmartSignalsProvider @Inject constructor(
    private val smartSignalsApi: SmartSignalsApi,
) {
    suspend fun getSmartSignals(
        requestId: String,
    ): SmartSignalsResponse {
        val baseUrl = Protected.smartSignalsBaseUrl
        val origin = Protected.smartSignalsOrigin
        if (baseUrl == null || origin == null)
            return Err(Error.EndpointInfoNotSetInApp)

        return runCatchingCancellable {
            smartSignalsApi.getSmartSignals(
                url = "${baseUrl}event/$requestId",
                origin = origin,
            )
        }
            .mapError { Error.Unknown }
            .map { parse(it) }
    }


    sealed class Error {
        data object EndpointInfoNotSetInApp : Error()
        data object Unknown : Error()
    }

    companion object {
        @VisibleForTesting
        fun parse(dto: SmartSignalsApi.SmartSignalsDto): SmartSignals {
            fun <I1, I2 : SmartSignalsApi.SmartSignalEntryDto<I1>, O : SmartSignal> I2?.toSignal(mapper: I2.() -> O): O? {
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

            val products = dto.products

            return SmartSignals(
                clonedApp = products.clonedApp.toSignal {
                    SmartSignal.ClonedApp(
                        detected = data!!.result!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.CLONED_APP_KEY,
                    )
                },
                emulator = products.emulator.toSignal {
                    SmartSignal.Emulator(
                        detected = data!!.result!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.EMULATOR_KEY,
                    )
                },
                factoryReset = products.factoryReset.toSignal {
                    SmartSignal.FactoryReset(
                        time = data!!.time!!.dropEssentiallyEmpty()!!,
                        timestamp = data.timestamp!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.FACTORY_RESET_KEY,
                    )
                },
                frida = products.frida.toSignal {
                    SmartSignal.Frida(
                        detected = data!!.result!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.FRIDA_KEY,
                    )
                },
                highActivity = products.highActivity.toSignal {
                    SmartSignal.HighActivity(
                        detected = data!!.result!!,
                        dailyRequests = data.dailyRequests,
                        rawObject = this,
                        rawKey = SmartSignalsApi.HIGH_ACTIVITY_KEY,
                    )
                },
                locationSpoofing = products.locationSpoofing.toSignal {
                    SmartSignal.LocationSpoofing(
                        detected = data!!.result!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.LOCATION_SPOOFING_KEY,
                    )
                },
                root = products.root.toSignal {
                    SmartSignal.Root(
                        detected = data!!.result!!,
                        rawObject = this,
                        rawKey = SmartSignalsApi.ROOT_KEY,
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
                        rawKey = SmartSignalsApi.VPN_KEY,
                    )
                },
            )
        }
    }
}
