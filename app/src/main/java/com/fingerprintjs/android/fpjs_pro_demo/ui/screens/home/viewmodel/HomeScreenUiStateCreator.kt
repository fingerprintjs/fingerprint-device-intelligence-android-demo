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
import com.fingerprintjs.android.fpjs_pro_demo.constants.StringConstants
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.FingerprintJSProResult
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalInfo
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignals
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsError
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.utils.detectionStatusString
import com.fingerprintjs.android.fpjs_pro_demo.utils.getEpochTimestampFromTimeString
import com.fingerprintjs.android.fpjs_pro_demo.utils.getProximityDetails
import com.fingerprintjs.android.fpjs_pro_demo.utils.getRelativeTimeString
import com.fingerprintjs.android.fpjs_pro_demo.utils.getVpnNoteString
import com.fingerprintjs.android.fpjs_pro_demo.utils.getVpnStatusString
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
    @Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod", "ReturnCount")
    fun HomeScreenUiState.Content.Companion.create(
        fingerprintSdkResponse: FingerprintJSProResult,
        smartSignalsData: SmartSignalsData,
        isLoading: Boolean,
        isAnyLocationPermissionGranted: Boolean = false,
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
            isAnyLocationPermissionGranted = isAnyLocationPermissionGranted,
            onHideSignupPrompt = onHideSignupPrompt,
            onPutToClipboard = onPutToClipboard,
            onSmartSignalDocClicked = onSmartSignalDocClicked,
            onSignupPromptClicked = onSignupPromptClicked,
        )
    }

    @Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
    fun HomeScreenUiState.Content.LoadingOrSuccess.Companion.create(
        fingerprintJSProResponse: FingerprintJSProResponse,
        smartSignals: SmartSignals?, // null indicates that endpoint info is not set in the app
        isLoading: Boolean,
        isSmartSignalsLoading: Boolean,
        isAnyLocationPermissionGranted: Boolean = false,
        onSmartSignalDocClicked: (url: String) -> Unit = {},
        onHideSignupPrompt: () -> Unit = {},
        onPutToClipboard: (String) -> Unit = {},
        onSignupPromptClicked: () -> Unit = {},
    ): HomeScreenUiState.Content.LoadingOrSuccess {
        // Checking the values from FingerprintJSProResponse for unavailability
        // is very inconvenient now. It will be improved in the future releases of the SDK.
        fun String.dropEssentiallyEmpty(): String? = takeIf {
            it.isNotEmpty() &&
                it != StringConstants.N_A_ESCAPED &&
                !it.contentEquals(StringConstants.NULL_STRING, ignoreCase = true)
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
        val firstSeenAtTimestamp = firstSeenAt?.let { getEpochTimestampFromTimeString(it) } ?: 0L
        val lastSeenAtTimestamp = lastSeenAt?.let { getEpochTimestampFromTimeString(it) } ?: 0L

        fun <T : SmartSignal> smartSignalProperty(
            from: SmartSignals.() -> SmartSignalInfo<T>,
            name: String,
            docUrl: String,
            value: T.() -> String,
            note: (T.() -> String?)? = null,
            smartSignalLinkText: String = StringConstants.SMART_SIGNAL,
        ): PrettifiedProperty {
            val smartSignalsInfo = smartSignals?.let(from)
            return PrettifiedProperty(
                name = name,
                value = when (smartSignalsInfo) {
                    null -> StringConstants.NOT_AVAILABLE
                    is SmartSignalInfo.Success -> smartSignalsInfo.typedData.value()
                    is SmartSignalInfo.Error -> StringConstants.NOT_AVAILABLE
                    is SmartSignalInfo.ParseError -> StringConstants.NOT_AVAILABLE
                    is SmartSignalInfo.Disabled -> StringConstants.SIGNAL_DISABLED
                },
                isValueFaded = smartSignalsInfo !is SmartSignalInfo.Success,
                isValueItalic = smartSignalsInfo is SmartSignalInfo.Disabled,
                isSmartSignal = true,
                onSmartSignalClick = { onSmartSignalDocClicked(docUrl) },
                note = (smartSignalsInfo as? SmartSignalInfo.Success)?.typedData?.let {
                    note?.invoke(
                        it
                    )
                },
                smartSignalLinkText = smartSignalLinkText,
            )
        }

        fun identificationProperty(
            name: String,
            value: String?,
        ): PrettifiedProperty {
            return PrettifiedProperty(
                name = name,
                value = value ?: StringConstants.NOT_AVAILABLE,
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
                    name = StringConstants.REQUEST_ID,
                    value = requestId
                ),
                identificationProperty(
                    name = StringConstants.VISITOR_ID,
                    value = visitorId
                ),
                identificationProperty(
                    name = StringConstants.VISITOR_FOUND,
                    value = if (visitorFound) StringConstants.YES else StringConstants.NO
                ),
                identificationProperty(
                    name = StringConstants.CONFIDENCE,
                    value = "${round(confidence * 100).toInt()}${StringConstants.PERCENTAGE}"
                ),
                identificationProperty(
                    name = StringConstants.IP_ADDRESS,
                    value = ipAddress
                ),
                identificationProperty(
                    name = StringConstants.FIRST_SEEN_AT,
                    value = getRelativeTimeString(firstSeenAt, firstSeenAtTimestamp)
                ),
                identificationProperty(
                    name = StringConstants.LAST_SEEN_AT,
                    value = getRelativeTimeString(lastSeenAt, lastSeenAtTimestamp)
                ),

                smartSignalProperty(
                    from = { clonedApp },
                    name = StringConstants.CLONED_APP,
                    docUrl = URLs.SmartSignalsOverview.clonedApp,
                    value = {
                        result.detectionStatusString()
                    },
                ),
                smartSignalProperty(
                    from = { emulator },
                    name = StringConstants.EMULATOR,
                    docUrl = URLs.SmartSignalsOverview.emulator,
                    value = {
                        result.detectionStatusString()
                    },
                ),
                smartSignalProperty(
                    from = { factoryReset },
                    name = StringConstants.FACTORY_RESET,
                    docUrl = URLs.SmartSignalsOverview.factoryReset,
                    value = {
                        getRelativeTimeString(time, timestamp)
                    },
                ),
                smartSignalProperty(
                    from = { frida },
                    name = StringConstants.FRIDA,
                    docUrl = URLs.SmartSignalsOverview.frida,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { highActivity },
                    name = StringConstants.HIGH_ACTIVITY,
                    docUrl = URLs.SmartSignalsOverview.highActivity,
                    value = {
                        if (result && dailyRequests != null) {
                            "${StringConstants.DETECTED}. $dailyRequests ${StringConstants.PER_DAY}"
                        } else {
                            result.detectionStatusString()
                        }
                    }
                ),
                smartSignalProperty(
                    from = { ipBlocklist },
                    name = StringConstants.IP_BLOCKLIST_MATCH,
                    docUrl = URLs.SmartSignalsOverview.ipBlocklist,
                    value = {
                        result.detectionStatusString()
                    }
                ),

                smartSignalProperty(
                    from = { ipInfo },
                    name = StringConstants.IP_LOCATION,
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
                    name = StringConstants.IP_NETWORK_PROVIDER,
                    docUrl = URLs.SmartSignalsOverview.ipNetworkProvider,
                    value = {
                        "${v4.asn.name} - ${v4.asn.asn}"
                    }
                ),

                smartSignalProperty(
                    from = { locationSpoofing },
                    name = StringConstants.GEOLOCATION_SPOOFING,
                    docUrl = URLs.SmartSignalsOverview.locationSpoofing,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { mitm },
                    name = StringConstants.MITM_ATTACK,
                    docUrl = URLs.SmartSignalsOverview.mitm,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { proxy },
                    name = StringConstants.PROXY,
                    docUrl = URLs.SmartSignalsOverview.proxy,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { root },
                    name = StringConstants.ROOTED_DEVICE,
                    docUrl = URLs.SmartSignalsOverview.root,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { tampering },
                    name = StringConstants.TAMPERED_REQUEST,
                    docUrl = URLs.SmartSignalsOverview.tampering,
                    value = {
                        result.detectionStatusString()
                    }
                ),
                smartSignalProperty(
                    from = { vpn },
                    name = StringConstants.VPN,
                    docUrl = URLs.SmartSignalsOverview.vpn,
                    value = { getVpnStatusString() },
                    note = { getVpnNoteString() },
                ),
                smartSignalProperty(
                    from = { proximity },
                    name = StringConstants.PROXIMITY,
                    docUrl = StringConstants.PROXIMITY_DOC_URL,
                    value = { getProximityDetails(isAnyLocationPermissionGranted) },
                    smartSignalLinkText = StringConstants.MORE_INFO,
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

    @VisibleForTesting
    fun createRawJson(
        fingerprintJSProResponse: FingerprintJSProResponse,
        smartSignals: SmartSignals?,
    ): String {
        val map = buildMap {
            this.put(StringConstants.IDENTIFICATION, fingerprintJSProResponse.toJsonMap())
            if (smartSignals != null) {
                this.put(
                    StringConstants.SMART_SIGNALS,
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
                            smartSignals.mitm,
                            smartSignals.proxy,
                            smartSignals.root,
                            smartSignals.tampering,
                            smartSignals.vpn,
                            smartSignals.proximity,
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
}
