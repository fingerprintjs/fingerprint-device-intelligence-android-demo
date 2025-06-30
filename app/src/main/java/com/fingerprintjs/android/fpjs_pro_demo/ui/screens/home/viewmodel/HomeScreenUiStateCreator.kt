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
import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.NotAvailableForCrawlBots
import com.fingerprintjs.android.fpjs_pro.NotAvailableWithoutUA
import com.fingerprintjs.android.fpjs_pro.OriginNotAvailable
import com.fingerprintjs.android.fpjs_pro.PackageNotAuthorized
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
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsResponse
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonMap
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonObject
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.recoverIf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class HomeScreenUiStateCreator @Inject constructor(
    private val json: Json,
) {

    fun HomeScreenUiState.Content.Companion.create(
        fingerprintSdkResponse: FingerprintJSProResult,
        smartSignalsResponse: SmartSignalsResponse,
        isLoading: Boolean,
        onSmartSignalDocClicked: (url: String) -> Unit = {},
        onHideSignupPrompt: () -> Unit = {},
        onPutToClipboard: (String) -> Unit = {},
        onSignupPromptClicked: () -> Unit = {},
        onReload: () -> Unit = {},
        onSupportClicked: () -> Unit = {},
        onGotoApiKeysSettings: () -> Unit = {},
    ): HomeScreenUiState.Content {

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
                }
            }

        val smartSignalsSuccessResult = smartSignalsResponse
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

        return HomeScreenUiState.Content.LoadingOrSuccess.create(
            fingerprintJSProResponse = fingerprintSuccessResult,
            smartSignals = smartSignalsSuccessResult,
            isLoading = isLoading,
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
        onSmartSignalDocClicked: (url: String) -> Unit = {},
        onHideSignupPrompt: () -> Unit = {},
        onPutToClipboard: (String) -> Unit = {},
        onSignupPromptClicked: () -> Unit = {},
    ): HomeScreenUiState.Content.LoadingOrSuccess {
        // Checking the values from FingerprintJSProResponse for unavailability
        // is very inconvenient now. It will be improved in the future releases of the SDK.
        fun String.dropEssentiallyEmpty(): String? = takeIf {
            it.isNotEmpty()
                    && it != "n\\a"
                    && !it.contentEquals("null", ignoreCase = true)
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
                    name = "IP Location",
                    value = run {
                        when {
                            ipCountry != null && ipCity != null -> "$ipCity, $ipCountry"
                            ipCountry != null -> "$ipCountry"
                            else -> null
                        }
                    },
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
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { emulator },
                    name = "Emulator",
                    docUrl = URLs.SmartSignalsOverview.emulator
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { factoryReset },
                    name = "Factory Reset",
                    docUrl = URLs.SmartSignalsOverview.factoryReset,
                ) {
                    if (timestamp <= 0) NOT_DETECTED_STRING else time
                },
                smartSignalProperty(
                    from = { frida },
                    name = "Frida",
                    docUrl = URLs.SmartSignalsOverview.frida,
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { highActivity },
                    name = "High Activity",
                    docUrl = URLs.SmartSignalsOverview.highActivity,
                ) {
                    if (result && dailyRequests != null)
                        "${DETECTED_STRING}. $dailyRequests per day"
                    else
                        result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { locationSpoofing },
                    name = "Geolocation Spoofing",
                    docUrl = URLs.SmartSignalsOverview.locationSpoofing,
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { mitm },
                    name = "MITM Attack",
                    docUrl = URLs.SmartSignalsOverview.mitm
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { root },
                    name = "Rooted Device",
                    docUrl = URLs.SmartSignalsOverview.root,
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { tampering },
                    name = "Tampered Request",
                    docUrl = URLs.SmartSignalsOverview.tampering
                ) {
                    result.detectionStatusString()
                },
                smartSignalProperty(
                    from = { vpn },
                    name = "VPN",
                    docUrl = URLs.SmartSignalsOverview.vpn,
                ) {
                    when {
                        !result -> NOT_DETECTED_STRING
                        originCountry != null ->
                            "${DETECTED_STRING}. Device location is $originCountry"

                        originTimezone != null ->
                            "${DETECTED_STRING}. Device timezone is $originTimezone"

                        else -> DETECTED_STRING
                    }
                },
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

    @VisibleForTesting
    fun createRawJson(
        fingerprintJSProResponse: FingerprintJSProResponse,
        smartSignals: SmartSignals?,
    ): String {
        val map = buildMap {
            this.put("identification", fingerprintJSProResponse.toJsonMap())
            if (smartSignals != null) {
                this.put("smartSignals", buildMap {
                    listOfNotNull(
                        smartSignals.clonedApp,
                        smartSignals.emulator,
                        smartSignals.factoryReset,
                        smartSignals.frida,
                        smartSignals.highActivity,
                        smartSignals.locationSpoofing,
                        smartSignals.root,
                        smartSignals.vpn,
                    )
                        .forEach {
                            if (it is SmartSignalInfo.WithRawData) {
                                this.put(it.rawKey, it.rawData)
                            }
                        }
                })
            }
        }
        return json.encodeToString(map.toJsonObject())
    }


    private fun Boolean.detectionStatusString(): String {
        return if (this) DETECTED_STRING else NOT_DETECTED_STRING
    }

    private val NOT_DETECTED_STRING = "Not detected"
    private val DETECTED_STRING = "Detected"
    private val NOT_AVAILABLE_STRING = "N/A"
}
