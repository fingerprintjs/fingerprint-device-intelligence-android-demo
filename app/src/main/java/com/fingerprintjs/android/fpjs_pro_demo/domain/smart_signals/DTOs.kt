package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class SmartSignalsDto(
    @JsonProperty("products")
    val products: ProductsDto
) {
    data class ProductsDto(
        @JsonProperty(CLONED_APP_KEY)
        val clonedApp: SmartSignalEntryDto<ClonedAppDto>?,
        @JsonProperty(EMULATOR_KEY)
        val emulator: SmartSignalEntryDto<EmulatorDto>?,
        @JsonProperty(FACTORY_RESET_KEY)
        val factoryReset: SmartSignalEntryDto<FactoryResetDto>?,
        @JsonProperty(FRIDA_KEY)
        val frida: SmartSignalEntryDto<FridaDto>?,
        @JsonProperty(HIGH_ACTIVITY_KEY)
        val highActivity: SmartSignalEntryDto<HighActivityDto>?,
        @JsonProperty(LOCATION_SPOOFING_KEY)
        val locationSpoofing: SmartSignalEntryDto<LocationSpoofingDto>?,
        @JsonProperty(ROOT_KEY)
        val root: SmartSignalEntryDto<RootDto>?,
        @JsonProperty(VPN_KEY)
        val vpn: SmartSignalEntryDto<VpnDto>?,
    )

    data class SmartSignalEntryDto<T>(
        @JsonProperty("data")
        val data: T?
    )

    data class ClonedAppDto(
        @JsonProperty("result")
        val result: Boolean?
    )

    data class FactoryResetDto(
        @JsonProperty("time")
        val time: String?,
        @JsonProperty("timestamp")
        val timestamp: Int?,
    )

    data class FridaDto(
        @JsonProperty("result")
        val result: Boolean?
    )

    data class EmulatorDto(
        @JsonProperty("result")
        val result: Boolean?,
    )

    data class LocationSpoofingDto(
        @JsonProperty("result")
        val result: Boolean?
    )

    data class RootDto(
        @JsonProperty("result")
        val result: Boolean?
    )

    data class VpnDto(
        @JsonProperty("result")
        val result: Boolean?,
        @JsonProperty("originTimezone")
        val originTimezone: String?,
        @JsonProperty("originCountry")
        val originCountry: String?,
        @JsonProperty("methods")
        val methods: VpnMethodsDto?
    ) {
        data class VpnMethodsDto(
            @JsonProperty("timezoneMismatch")
            val timezoneMismatch: Boolean?,
            @JsonProperty("publicVPN")
            val publicVPN: Boolean?,
            @JsonProperty("auxiliaryMobile")
            val auxiliaryMobile: Boolean?,
            @JsonProperty("osMismatch")
            val osMismatch: Boolean?,
        )
    }

    data class HighActivityDto(
        @JsonProperty("result")
        val result: Boolean?,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("dailyRequests")
        val dailyRequests: Int?,
    )

    companion object {
        const val CLONED_APP_KEY = "clonedApp"
        const val EMULATOR_KEY = "emulator"
        const val FACTORY_RESET_KEY = "factoryReset"
        const val FRIDA_KEY = "frida"
        const val HIGH_ACTIVITY_KEY = "highActivity"
        const val LOCATION_SPOOFING_KEY = "locationSpoofing"
        const val ROOT_KEY = "rootApps"
        const val VPN_KEY = "vpn"
    }
}

data class SmartSignalsErrorDto(
    @JsonProperty("error")
    val error: ErrorDto
) {
    data class ErrorDto(
        @JsonProperty("code")
        val code: String?,
        @JsonProperty("message")
        val message: String?,
    )
}
