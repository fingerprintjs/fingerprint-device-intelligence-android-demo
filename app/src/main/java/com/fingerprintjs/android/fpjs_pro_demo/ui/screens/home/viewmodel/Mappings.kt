package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fingerprintjs.android.fpjs_pro.ApiKeyExpired
import com.fingerprintjs.android.fpjs_pro.ApiKeyNotFound
import com.fingerprintjs.android.fpjs_pro.ApiKeyRequired
import com.fingerprintjs.android.fpjs_pro.Configuration
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
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignals
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsError
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsResponse
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonMap
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.recoverIf
import kotlin.math.round

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

    val unknownError = HomeScreenUiState.Content.Error.Unknown(onSupportClicked = onSupportClicked, onReload = onReload)
    val networkError = HomeScreenUiState.Content.Error.Network(onReload = onReload)
    val secretApiKeyMismatchError = HomeScreenUiState.Content.Error.SecretApiKeyMismatch(onGotoApiKeysSettings = onGotoApiKeysSettings)

    val fingerprintSuccessResult = fingerprintSdkResponse
        .getOrElse { error ->
            return when (error) {
                is NetworkError -> networkError
                is TooManyRequest -> HomeScreenUiState.Content.Error.TooManyRequests(onReload = onReload)
                is ApiKeyExpired -> HomeScreenUiState.Content.Error.PublicApiKeyExpired(onGotoApiKeysSettings = onGotoApiKeysSettings)
                is ApiKeyNotFound -> HomeScreenUiState.Content.Error.PublicApiKeyInvalid(onGotoApiKeysSettings = onGotoApiKeysSettings)
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
                is SubscriptionNotActive -> HomeScreenUiState.Content.Error.SubscriptionNotActive(onGotoApiKeysSettings = onGotoApiKeysSettings)
                is UnknownError -> unknownError
                is UnsupportedVersion -> unknownError
                is WrongRegion -> HomeScreenUiState.Content.Error.WrongRegion(onGotoApiKeysSettings = onGotoApiKeysSettings)
            }
        }

    val smartSignalsSuccessResult = smartSignalsResponse
        .recoverIf(
            predicate = { it is SmartSignalsError.EndpointInfoNotSetInApp },
            transform = { SmartSignals.EMPTY }
        )
        .getOrElse { error ->
            return when (error) {
                SmartSignalsError.EndpointInfoNotSetInApp -> unknownError // unreachable
                SmartSignalsError.FeatureNotEnabled -> unknownError
                SmartSignalsError.RequestNotFound -> secretApiKeyMismatchError
                SmartSignalsError.SubscriptionNotActive -> secretApiKeyMismatchError
                SmartSignalsError.TokenNotFound -> HomeScreenUiState.Content.Error.SecretApiKeyInvalid(onGotoApiKeysSettings = onGotoApiKeysSettings)
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
    smartSignals: SmartSignals,
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
        from: T?,
        name: String,
        docUrl: String,
        value: T.() -> String,
    ): PrettifiedProperty? {
        return from?.let {
            PrettifiedProperty(
                name = name,
                value = it.value(),
                isSmartSignal = true,
                onSmartSignalClick = { onSmartSignalDocClicked(docUrl) },
            )
        }
    }

    // todo: improve logic for smart signals states (N/A, excluded in subscription, etc)
    return HomeScreenUiState.Content.LoadingOrSuccess(
        visitorId = fingerprintJSProResponse.visitorId,
        rawJson = runCatching {
            createRawJson(fingerprintJSProResponse, smartSignals)
        }.getOrNull(),
        isLoading = isLoading,
        isSignupPromptShown = false,
        prettifiedProps = listOfNotNull(
            PrettifiedProperty(
                name = "Request ID",
                value = requestId
            ),
            PrettifiedProperty(
                name = "Visitor ID",
                value = visitorId
            ),
            PrettifiedProperty(
                name = "Visitor Found",
                value = if (visitorFound) "Yes" else "No"
            ),
            PrettifiedProperty(
                name = "Confidence",
                value = "${round(confidence * 100).toInt()}%"
            ),
            PrettifiedProperty(
                name = "IP Address",
                value = ipAddress
            ),
            PrettifiedProperty(
                name = "IP Location",
                value = run {
                    when {
                        ipCountry != null && ipCity != null -> "$ipCity, $ipCountry"
                        ipCountry != null -> "$ipCountry"
                        else -> null
                    }
                },
            ),
            PrettifiedProperty(
                name = "First Seen At",
                value = firstSeenAt
            ),
            PrettifiedProperty(
                name = "Last Seen At",
                value = lastSeenAt
            ),
            smartSignalProperty(
                from = smartSignals.clonedApp,
                name = "Cloned App",
                docUrl = URLs.SmartSignalsOverview.clonedApp,
            ) {
                detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.emulator,
                name = "Emulator",
                docUrl = URLs.SmartSignalsOverview.emulator
            ) {
                detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.factoryReset,
                name = "Factory Reset",
                docUrl = URLs.SmartSignalsOverview.factoryReset,
            ) {
                if (timestamp <= 0) NOT_DETECTED_STRING else time
            },
            smartSignalProperty(
                from = smartSignals.frida,
                name = "Frida",
                docUrl = URLs.SmartSignalsOverview.frida,
            ) {
                detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.highActivity,
                name = "High Activity",
                docUrl = URLs.SmartSignalsOverview.highActivity,
            ) {
                if (detected && dailyRequests != null)
                    "${DETECTED_STRING}. $dailyRequests per day"
                else
                    detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.locationSpoofing,
                name = "Geolocation Spoofing",
                docUrl = URLs.SmartSignalsOverview.locationSpoofing,
            ) {
                detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.root,
                name = "Tamper",
                docUrl = URLs.SmartSignalsOverview.root,
            ) {
                detected.detectionStatusString()
            },
            smartSignalProperty(
                from = smartSignals.vpn,
                name = "VPN",
                docUrl = URLs.SmartSignalsOverview.vpn,
            ) {
                when {
                    !detected -> NOT_DETECTED_STRING
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
                    onLongClick = { onPutToClipboard(it.value.orEmpty()) },
                    onLongClickEnabled = it.value != null && !isLoading,
                )
            },
        onSignupPromptClicked = onSignupPromptClicked,
        onHideSignupPromptClicked = onHideSignupPrompt,
    )
}

fun createRawJson(
    fingerprintJSProResponse: FingerprintJSProResponse,
    smartSignals: SmartSignals,
): String {
    val mapper = JsonMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    val objectNode = mapper.createObjectNode()
    objectNode.put(
        "identification",
        mapper.valueToTree<JsonNode>(fingerprintJSProResponse.toJsonMap())
    )
    objectNode.putObject("smartSignals").apply {
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
                putPOJO(it.rawKey, it.rawObject)
            }
    }
    return objectNode.toPrettyString()
}

private fun Boolean.detectionStatusString(): String {
    return if (this) DETECTED_STRING else NOT_DETECTED_STRING
}

private const val NOT_DETECTED_STRING = "Not detected"
private const val DETECTED_STRING = "Detected"
