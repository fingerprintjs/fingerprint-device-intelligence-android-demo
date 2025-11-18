package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.annotation.VisibleForTesting
import com.fingerprintjs.android.fpjs_pro.ApiKeyExpired
import com.fingerprintjs.android.fpjs_pro.ApiKeyNotFound
import com.fingerprintjs.android.fpjs_pro.ApiKeyRequired
import com.fingerprintjs.android.fpjs_pro.ClientTimeout
import com.fingerprintjs.android.fpjs_pro.Failed
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.fingerprintjs.android.fpjs_pro.HeaderRestricted
import com.fingerprintjs.android.fpjs_pro.InstallationMethodRestricted
import com.fingerprintjs.android.fpjs_pro.InvalidProxyIntegrationHeaders
import com.fingerprintjs.android.fpjs_pro.InvalidProxyIntegrationSecret
import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.NotAvailableForCrawlBots
import com.fingerprintjs.android.fpjs_pro.NotAvailableWithoutUA
import com.fingerprintjs.android.fpjs_pro.OriginNotAvailable
import com.fingerprintjs.android.fpjs_pro.PackageNotAuthorized
import com.fingerprintjs.android.fpjs_pro.ProxyIntegrationSecretEnvironmentMismatch
import com.fingerprintjs.android.fpjs_pro.RequestCannotBeParsed
import com.fingerprintjs.android.fpjs_pro.RequestTimeout
import com.fingerprintjs.android.fpjs_pro.ResponseCannotBeParsed
import com.fingerprintjs.android.fpjs_pro.SubscriptionNotActive
import com.fingerprintjs.android.fpjs_pro.TooManyRequest
import com.fingerprintjs.android.fpjs_pro.UnknownError
import com.fingerprintjs.android.fpjs_pro.UnsupportedVersion
import com.fingerprintjs.android.fpjs_pro.WrongRegion
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.FingerprintJSProResult
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalInfo
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignals
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsError
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.utils.relativeFactoryResetTime
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonMap
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonObject
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.recoverIf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class HomeScreenUiStateCreator @Inject constructor(
    private val json: Json,
) {
    fun HomeScreenUiState.Content.Companion.create(
        fingerprintSdkResponse: FingerprintJSProResult,
        smartSignalsData: SmartSignalsData,
        isLoading: Boolean,
        onSmartSignalDocClicked: (url: String) -> Unit = {},
        onHideSignupPrompt: () -> Unit = {},
        onPutToClipboard: (String) -> Unit = {},
        onSignupPromptClicked: () -> Unit = {},
        onReload: () -> Unit = {},
        onSupportClicked: () -> Unit = {},
        onGotoApiKeysSettings: () -> Unit = {},
    ): HomeScreenUiState.Content {
        val isSmartSignalsLoading = smartSignalsData is SmartSignalsData.Loading

        val unknownError = HomeScreenUiState.Content.Error.Unknown(
            onSupportClicked = onSupportClicked,
            onReload = onReload
        )
        val networkError = HomeScreenUiState.Content.Error.Network(onReload = onReload)
        val secretApiKeyMismatchError =
            HomeScreenUiState.Content.Error.SecretApiKeyMismatch(onGotoApiKeysSettings = onGotoApiKeysSettings)

        val fingerprintSuccessResult = fingerprintSdkResponse
            .getOrElse { error ->
                return when (error) {
                    is NetworkError -> networkError
                    is TooManyRequest -> HomeScreenUiState.Content.Error.TooManyRequests(onReload = onReload)
                    is ApiKeyExpired -> HomeScreenUiState.Content.Error.PublicApiKeyExpired(
                        onGotoApiKeysSettings = onGotoApiKeysSettings
                    )

                    is ApiKeyNotFound -> HomeScreenUiState.Content.Error.PublicApiKeyInvalid(
                        onGotoApiKeysSettings = onGotoApiKeysSettings
                    )

                    is ApiKeyRequired -> unknownError
                    is Failed -> unknownError
                    is HeaderRestricted -> unknownError
                    is InstallationMethodRestricted -> unknownError
                    is NotAvailableForCrawlBots -> unknownError
                    is NotAvailableWithoutUA -> unknownError
                    is OriginNotAvailable -> unknownError
                    is PackageNotAuthorized -> unknownError
                    is RequestCannotBeParsed -> unknownError
                    is RequestTimeout -> unknownError
                    is ResponseCannotBeParsed -> unknownError
                    is SubscriptionNotActive -> HomeScreenUiState.Content.Error.SubscriptionNotActive(
                        onGotoApiKeysSettings = onGotoApiKeysSettings
                    )

                    is UnknownError -> unknownError
                    is UnsupportedVersion -> unknownError
                    is WrongRegion -> HomeScreenUiState.Content.Error.WrongRegion(
                        onGotoApiKeysSettings = onGotoApiKeysSettings
                    )

                    is ClientTimeout -> networkError
                    is InvalidProxyIntegrationHeaders,
                    is InvalidProxyIntegrationSecret,
                    is ProxyIntegrationSecretEnvironmentMismatch -> HomeScreenUiState.Content.Error.Generic(
                        error = error,
                        onReload = onReload,
                    )
                }
            }

        val smartSignalsSuccessResult: SmartSignals? =
            if (smartSignalsData is SmartSignalsData.Data) {
                smartSignalsData.smartSignalsResponse
                    .recoverIf(
                        predicate = { it is SmartSignalsError.EndpointInfoNotSetInApp },
                        transform = { null }
                    )
                    .getOrElse { error ->
                        return when (error) {
                            SmartSignalsError.EndpointInfoNotSetInApp -> unknownError // unreachable
                            SmartSignalsError.FeatureNotEnabled -> unknownError
                            SmartSignalsError.RequestNotFound -> secretApiKeyMismatchError
                            SmartSignalsError.SubscriptionNotActive -> secretApiKeyMismatchError
                            SmartSignalsError.TokenNotFound -> HomeScreenUiState.Content.Error.SecretApiKeyInvalid(
                                onGotoApiKeysSettings = onGotoApiKeysSettings
                            )

                            SmartSignalsError.TokenRequired -> unknownError
                            SmartSignalsError.UnknownApiError -> unknownError
                            SmartSignalsError.WrongRegion -> secretApiKeyMismatchError
                            is SmartSignalsError.NetworkError -> networkError
                            SmartSignalsError.ParseError -> unknownError
                            SmartSignalsError.Unknown -> unknownError
                        }
                    }
            } else {
                null
            }

        return HomeScreenUiState.Content.LoadingOrSuccess.create(
            fingerprintJSProResponse = fingerprintSuccessResult,
            smartSignals = smartSignalsSuccessResult,
            isLoading = isLoading,
            isSmartSignalsLoading = isSmartSignalsLoading,
            onHideSignupPrompt = onHideSignupPrompt,
            onPutToClipboard = onPutToClipboard,
            onSmartSignalDocClicked = onSmartSignalDocClicked,
            onSignupPromptClicked = onSignupPromptClicked,
        )
    }

    fun HomeScreenUiState.Content.LoadingOrSuccess.Companion.create(
        fingerprintJSProResponse: FingerprintJSProResponse,
        smartSignals: SmartSignals?, // null indicates that endpoint info is not set in the app
        isLoading: Boolean,
        isSmartSignalsLoading: Boolean,
        onSmartSignalDocClicked: (url: String) -> Unit = {},
        onHideSignupPrompt: () -> Unit = {},
        onPutToClipboard: (String) -> Unit = {},
        onSignupPromptClicked: () -> Unit = {},
    ): HomeScreenUiState.Content.LoadingOrSuccess {
        // Checking the values from FingerprintJSProResponse for unavailability
        // is very inconvenient now. It will be improved in the future releases of the SDK.
        fun String.dropEssentiallyEmpty(): String? = takeIf {
            it.isNotEmpty() &&
                    it != "n\\a" &&
                    !it.contentEquals("null", ignoreCase = true)
        }

        val requestId = fingerprintJSProResponse.requestId.dropEssentiallyEmpty()
        val visitorId = fingerprintJSProResponse.visitorId
        val visitorFound = fingerprintJSProResponse.visitorFound
        val confidence = fingerprintJSProResponse.confidenceScore.score
        val ipAddress = fingerprintJSProResponse.ipAddress.dropEssentiallyEmpty()
        val ipCity = fingerprintJSProResponse.ipLocation?.city?.name?.dropEssentiallyEmpty()
        val ipCountry = fingerprintJSProResponse.ipLocation?.country?.name?.dropEssentiallyEmpty()
        val firstSeenAt = fingerprintJSProResponse.firstSeenAt.subscription.dropEssentiallyEmpty()
        val lastSeenAt = fingerprintJSProResponse.lastSeenAt.subscription.dropEssentiallyEmpty()

        fun <T : SmartSignal> smartSignalProperty(
            from: SmartSignals.() -> SmartSignalInfo<T>,
            name: String,
            docUrl: String,
            value: T.() -> String,
            note: (T.() -> String?)? = null,
        ): PrettifiedProperty {
            val smartSignalsInfo = smartSignals?.let(from)
            return PrettifiedProperty(
                name = name,
                value = when (smartSignalsInfo) {
                    null -> NOT_AVAILABLE_STRING
                    is SmartSignalInfo.Success -> smartSignalsInfo.typedData.value()
                    is SmartSignalInfo.Error -> NOT_AVAILABLE_STRING
                    is SmartSignalInfo.ParseError -> NOT_AVAILABLE_STRING
                    is SmartSignalInfo.Disabled -> "Signal disabled for your account"
                },
                isValueFaded = smartSignalsInfo !is SmartSignalInfo.Success,
                isValueItalic = smartSignalsInfo is SmartSignalInfo.Disabled,
                isSmartSignal = true,
                onSmartSignalClick = { onSmartSignalDocClicked(docUrl) },
                note = (smartSignalsInfo as? SmartSignalInfo.Success)?.typedData?.let {
                    note?.invoke(
                        it
                    )
                }
            )
        }

        fun identificationProperty(
            name: String,
            value: String?,
        ): PrettifiedProperty {
            return PrettifiedProperty(
                name = name,
                value = value ?: NOT_AVAILABLE_STRING,
                isValueFaded = value == null,
            )
        }

        return HomeScreenUiState.Content.LoadingOrSuccess(
            visitorId = fingerprintJSProResponse.visitorId,
            rawJson = runCatching {
                createRawJson(fingerprintJSProResponse, smartSignals)
            }.getOrNull(),
            isLoading = isLoading,
            isSmartSignalsLoading = isSmartSignalsLoading,
            isSignupPromptShown = false,
            prettifiedProps = listOfNotNull(
                identificationProperty(
                    name = "Request ID",
                    value = requestId
                ),
                identificationProperty(
                    name = "Visitor ID",
                    value = visitorId
                ),
                identificationProperty(
                    name = "Visitor Found",
                    value = if (visitorFound) "Yes" else "No"
                ),
                identificationProperty(
                    name = "Confidence",
                    value = "${round(confidence * 100).toInt()}%"
                ),
                identificationProperty(
                    name = "IP Address",
                    value = ipAddress
                ),
                identificationProperty(
                    name = "First Seen At",
                    value = firstSeenAt
                ),
                identificationProperty(
                    name = "Last Seen At",
                    value = lastSeenAt
                ),

                smartSignalProperty(
                    from = { clonedApp },
                    name = "Cloned App",
                    docUrl = URLs.SmartSignalsOverview.clonedApp,
                    value = {
                        result.detectionStatusString()
                    },
                ),
                smartSignalProperty(
                    from = { emulator },
                    name = "Emulator",
                    docUrl = URLs.SmartSignalsOverview.emulator,
                    value = {
                        result.detectionStatusString()
                    },
                ),
                smartSignalProperty(
                    from = { factoryReset },
                    name = "Factory Reset",
                    docUrl = URLs.SmartSignalsOverview.factoryReset,
                    value = {
                        if (timestamp <= 0 || time.isBlank()) {
                            NOT_DETECTED_STRING
                        } else {
                            relativeFactoryResetTime(time, timestamp)
                        }
                    },
                ),
                smartSignalProperty(
                    from = { frida },
                    name = "Frida",
                    docUrl = URLs.SmartSignalsOverview.frida,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { highActivity },
                    name = "High Activity",
                    docUrl = URLs.SmartSignalsOverview.highActivity,
                    value = {
                        if (result && dailyRequests != null) {
                            "${DETECTED_STRING}. $dailyRequests per day"
                        } else {
                            result.detectionStatusString()
                        }
                    }
                ),
                smartSignalProperty(
                    from = { ipBlocklist },
                    name = "IP Blocklist Match",
                    docUrl = URLs.SmartSignalsOverview.ipBlocklist,
                    value = {
                        result.detectionStatusString()
                    }
                ),

                smartSignalProperty(
                    from = { ipInfo },
                    name = "IP Location",
                    docUrl = URLs.SmartSignalsOverview.ipNetworkProvider,
                    value = {
                        when {
                            ipCountry != null && ipCity != null -> "$ipCity, $ipCountry"
                            ipCountry != null -> ipCountry
                            else -> "${v4.geolocation.city.name}, ${v4.geolocation.country.name}"
                        }
                    }
                ),

                smartSignalProperty(
                    from = { ipInfo },
                    name = "IP Network Provider",
                    docUrl = URLs.SmartSignalsOverview.ipNetworkProvider,
                    value = {
                        "${v4.asn.name} - ${v4.asn.asn}"
                    }
                ),

                smartSignalProperty(
                    from = { locationSpoofing },
                    name = "Geolocation Spoofing",
                    docUrl = URLs.SmartSignalsOverview.locationSpoofing,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { mitm },
                    name = "MITM Attack",
                    docUrl = URLs.SmartSignalsOverview.mitm,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { proxy },
                    name = "Proxy",
                    docUrl = URLs.SmartSignalsOverview.proxy,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { root },
                    name = "Rooted Device",
                    docUrl = URLs.SmartSignalsOverview.root,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { tampering },
                    name = "Tampered Request",
                    docUrl = URLs.SmartSignalsOverview.tampering,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { vpn },
                    name = "VPN",
                    docUrl = URLs.SmartSignalsOverview.vpn,
                    value = { getVpnStatusString() },
                    note = { getVpnNoteString() },
                ),
            )
                .map {
                    it.copy(
                        onLongClick = { onPutToClipboard(it.value) },
                        onLongClickEnabled = !isLoading,
                    )
                },
            onSignupPromptClicked = onSignupPromptClicked,
            onHideSignupPromptClicked = onHideSignupPrompt,
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun SmartSignal.Vpn.getVpnStatusString(): String = when {
        !result -> NOT_DETECTED_STRING

        else -> getVpnDetectionDetails(methods, confidence, originCountry)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun SmartSignal.Vpn.getVpnNoteString(): String = when {
        !result -> ""

        else -> VPN_NOTE_STRING
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getVpnDetectionDetails(
        methods: Map<String, Boolean>,
        confidence: String?,
        originCountry: String?
    ): String {
        val priority = listOf("publicVPN", "timezoneMismatch", "relay", "auxiliaryMobile")

        var detectedMethod = ""
        for (key in priority) {
            if (methods[key] == true) {
                detectedMethod = " (${prettifiedKeys[key]})"
                break
            }
        }

        return "${DETECTED_STRING}${detectedMethod}${appendConfidenceLevel(confidence)}${
            appendCountryInfo(
                originCountry
            )
        }"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun appendCountryInfo(originCountry: String?): String {
        return if (originCountry != null) {
            val countryInfo = getCountryInfo(originCountry)
            "\nOrigin Country: ${countryInfo.first} ${countryInfo.second}"
        } else {
            ""
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun appendConfidenceLevel(confidence: String?): String {
        return if (confidence != null) {
            "\nConfidence: $confidence"
        } else {
            ""
        }
    }

    @VisibleForTesting
    fun createRawJson(
        fingerprintJSProResponse: FingerprintJSProResponse,
        smartSignals: SmartSignals?,
    ): String {
        val map = buildMap {
            this.put("identification", fingerprintJSProResponse.toJsonMap())
            if (smartSignals != null) {
                this.put(
                    "smartSignals",
                    buildMap {
                        listOfNotNull(
                            smartSignals.clonedApp,
                            smartSignals.emulator,
                            smartSignals.factoryReset,
                            smartSignals.frida,
                            smartSignals.highActivity,
                            smartSignals.ipInfo,
                            smartSignals.ipBlocklist,
                            smartSignals.locationSpoofing,
                            smartSignals.proxy,
                            smartSignals.root,
                            smartSignals.vpn,
                        )
                            .forEach {
                                if (it is SmartSignalInfo.WithRawData) {
                                    this.put(it.rawKey, it.rawData)
                                }
                            }
                    }
                )
            }
        }
        return json.encodeToString(map.toJsonObject()).replace("""\\""", """\""")
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getFlagEmoji(countryCode: String): String {
        val normalized = countryCode.trim().uppercase(Locale.ROOT)
        if (normalized.length != 2) return ""

        val first = Character.codePointAt(normalized, 0) - 0x41 + 0x1F1E6
        val second = Character.codePointAt(normalized, 1) - 0x41 + 0x1F1E6

        return String(Character.toChars(first)) + String(Character.toChars(second))
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getCountryInfo(code: String): Pair<String, String> {
        val normalized = code.trim().uppercase(Locale.ROOT)
        val name = Locale("", normalized).displayCountry
        val flag = getFlagEmoji(normalized)
        val displayName = if (name.isNotBlank()) name else normalized
        return Pair(displayName, flag)
    }

    private fun Boolean.detectionStatusString(): String {
        return if (this) DETECTED_STRING else NOT_DETECTED_STRING
    }

    private val NOT_DETECTED_STRING = "Not detected"
    private val DETECTED_STRING = "Detected"
    private val NOT_AVAILABLE_STRING = "N/A"
    private val VPN_NOTE_STRING = "Note: works without location permissions"
    private val prettifiedKeys = mapOf(
        "publicVPN" to "Public VPN",
        "timezoneMismatch" to "Timezone mismatch",
        "relay" to "Relay",
        "auxiliaryMobile" to "Auxiliary mobile"
    )
}
