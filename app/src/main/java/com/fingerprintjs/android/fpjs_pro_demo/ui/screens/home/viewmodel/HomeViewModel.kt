package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.TooManyRequest
import com.fingerprintjs.android.fpjs_pro_demo.BuildConfig
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.domain.FingerprintJSProResult
import com.fingerprintjs.android.fpjs_pro_demo.domain.IdentificationProvider
import com.fingerprintjs.android.fpjs_pro_demo.domain.ShowSignUpPromptUseCase
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignals
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsProvider
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsResponse
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonMap
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import kotlin.math.round

class HomeViewModel @Inject constructor(
    private val identificationProvider: IdentificationProvider,
    private val showSignUpPromptUseCase: ShowSignUpPromptUseCase,
    private val smartSignalsProvider: SmartSignalsProvider,
) : ViewModel() {

    private val loadingState = (HomeScreenUiState.from(
        FingerprintData(
            fingerprintSdkResponse = Ok(StateMocks.fingerprintJSResponse),
            smartSignalsResponse = Ok(StateMocks.smartSignals)
        ),
        isLoading = true,
    ) as HomeScreenUiState.LoadingOrSuccess)

    private val isLoading = MutableStateFlow(false)
    private val fingerprintStarted = MutableSharedFlow<Unit>(replay = 1)
    private val fingerprintData = MutableStateFlow<FingerprintData?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeScreenUiState> =
        fingerprintStarted.flatMapLatest {
            combine(
                isLoading,
                fingerprintData,
            ) { isLoading, fingerprintData ->
                when {
                    isLoading || fingerprintData == null -> loadingState
                    else -> {
                        HomeScreenUiState.from(fingerprintData, isLoading = false)
                    }
                }
            }
                .onEach {
                    if (it is HomeScreenUiState.LoadingOrSuccess && !it.isLoading)
                        showSignUpPromptUseCase.onFingerprintSuccess()
                }
                .combine(showSignUpPromptUseCase.showAllowed) { uiState, showAllowed ->
                    if (uiState is HomeScreenUiState.LoadingOrSuccess && !uiState.isLoading) {
                        uiState.copy(isSignupPromptShown = showAllowed)
                    } else {
                        uiState
                    }
                }
        }
            .stateIn(
                scope = viewModelScope + Dispatchers.IO,
                started = SharingStarted.Eagerly,
                initialValue = HomeScreenUiState.TapToBegin(
                    onTap = ::onBegin,
                    onReload = ::onReload,
                    onSupportClicked = ::onSupportClicked,
                    onDocumentationClicked = ::onDocumentationClicked,
                    onSignupClicked = ::onSignupClicked,
                ),
            )

    private val mockingMutableState = MutableStateFlow(if (BuildConfig.ALLOW_MOCKS) false else null)
    val mockingState: StateFlow<Boolean?> = mockingMutableState

    private val externalLinkToOpenMutable = MutableSharedFlow<String>()
    val externalLinkToOpen: Flow<String>
        get() = externalLinkToOpenMutable

    private val textToCopyMutable = MutableSharedFlow<String>()
    val textToCopy: Flow<String>
        get() = textToCopyMutable

    fun onToggleMocking() {
        mockingMutableState.value?.let {
            mockingMutableState.value = !it
        }
    }

    private fun onBegin() {
        viewModelScope.launch {
            fingerprintStarted.emit(Unit)
            onReload()
        }
    }

    private fun onReload() {
        viewModelScope.launch {
            isLoading.value = true
            fingerprintData.value = getRealOrStubbedFingerprintData()
            isLoading.value = false
        }
    }

    private fun onSupportClicked() = onLaunchUrl(URLs.support)
    private fun onDocumentationClicked() = onLaunchUrl(URLs.documentation)
    private fun onSignupClicked() = onLaunchUrl(URLs.signup)

    private fun onHideSignupPromptClicked() {
        viewModelScope.launch { showSignUpPromptUseCase.onHideRequested() }
    }

    private fun onLaunchUrl(url: String) {
        viewModelScope.launch { externalLinkToOpenMutable.emit(url) }
    }

    private fun onPutToClipboard(text: String) {
        viewModelScope.launch { textToCopyMutable.emit(text) }
    }

    private var reloadCount = 0
    private suspend fun getRealOrStubbedFingerprintData(): FingerprintData {
        return withContext(Dispatchers.IO) {
            if (mockingState.value == true) {
                delay(2000)
                if (++reloadCount % 4 == 0)
                    FingerprintData(
                        fingerprintSdkResponse = Err(StateMocks.fingerprintJSOtherError),
                        smartSignalsResponse = Err(SmartSignalsProvider.Error.Unknown)
                    )
                else FingerprintData(
                    fingerprintSdkResponse = Ok(StateMocks.fingerprintJSResponse),
                    smartSignalsResponse = Ok(StateMocks.smartSignals),
                )
            } else {
                val fingerprintSdkResponse = identificationProvider.getVisitorId()
                val smartSignalsResponse = fingerprintSdkResponse
                    // if fingerprintSdkResponse is error, consider smart signals response as
                    // error too for simplicity
                    .mapError { SmartSignalsProvider.Error.Unknown }
                    .flatMap { smartSignalsProvider.getSmartSignals(it.requestId) }

                FingerprintData(
                    fingerprintSdkResponse = fingerprintSdkResponse,
                    smartSignalsResponse = smartSignalsResponse,
                )
            }
        }
    }

    private data class FingerprintData(
        val fingerprintSdkResponse: FingerprintJSProResult,
        val smartSignalsResponse: SmartSignalsResponse,
    )

    private fun HomeScreenUiState.Companion.from(
        data: FingerprintData,
        isLoading: Boolean,
    ): HomeScreenUiState {

        val fingerprintSuccessResult = data.fingerprintSdkResponse
            .getOrElse { error ->
                return when (error) {
                    is NetworkError -> HomeScreenUiState.Error.Network
                    is TooManyRequest -> HomeScreenUiState.Error.TooManyRequests
                    else -> HomeScreenUiState.Error.Unknown
                }
            }

        val smartSignalsSuccessResult = data.smartSignalsResponse
            .getOrElse { error ->
                when (error) {
                    SmartSignalsProvider.Error.EndpointInfoNotSetInApp -> SmartSignals.EMPTY
                    SmartSignalsProvider.Error.Unknown -> return HomeScreenUiState.Error.Unknown
                }
            }

        return createSuccessOrLoadingState(
            fingerprintJSProResponse = fingerprintSuccessResult,
            smartSignals = smartSignalsSuccessResult,
            isLoading = isLoading,
            onReload = ::onReload,
            onHideSignupPrompt = ::onHideSignupPromptClicked,
            onPutToClipboard = ::onPutToClipboard,
            onSmartSignalDocClicked = ::onLaunchUrl,
            onSignupPromptClicked = ::onSignupClicked,
            onSupportClicked = ::onSupportClicked,
            onDocumentationClicked = ::onDocumentationClicked,
            onSignupClicked = ::onSignupClicked,
        )
    }

    private val HomeScreenUiState.Error.Companion.Unknown
        get() = HomeScreenUiState.Error(
            image = Icons.Outlined.ErrorOutline,
            title = "An unexpected error occurred...",
            description = " Please contact support if this issue persists.",
            links = listOf(
                LinkableText.Link(
                    mask = "contact support",
                    handler = ::onSupportClicked,
                )
            ),
            onButtonCLick = ::onReload,
            onReload = ::onReload,
            onSupportClicked = ::onSupportClicked,
            onDocumentationClicked = ::onDocumentationClicked,
            onSignupClicked = ::onSignupClicked,
        )

    private val HomeScreenUiState.Error.Companion.Network
        get() = HomeScreenUiState.Error(
            image = Icons.Outlined.CloudOff,
            title = "Server cannot be reached",
            description = "Please check your network settings and try again.",
            links = emptyList(),
            onButtonCLick = ::onReload,
            onReload = ::onReload,
            onSupportClicked = ::onSupportClicked,
            onDocumentationClicked = ::onDocumentationClicked,
            onSignupClicked = ::onSignupClicked,
        )

    private val HomeScreenUiState.Error.Companion.TooManyRequests
        get() = HomeScreenUiState.Error(
            image = Icons.Outlined.BackHand,
            title = "Too many requests",
            description = " The request rate limit set for the public API key was exceeded.",
            links = emptyList(),
            onButtonCLick = ::onReload,
            onReload = ::onReload,
            onSupportClicked = ::onSupportClicked,
            onDocumentationClicked = ::onDocumentationClicked,
            onSignupClicked = ::onSignupClicked,
        )

    companion object {

        @VisibleForTesting
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


        @VisibleForTesting
        fun createSuccessOrLoadingState(
            fingerprintJSProResponse: FingerprintJSProResponse,
            smartSignals: SmartSignals,
            isLoading: Boolean,
            onReload: () -> Unit = {},
            onSmartSignalDocClicked: (url: String) -> Unit = {},
            onHideSignupPrompt: () -> Unit = {},
            onPutToClipboard: (String) -> Unit = {},
            onSignupPromptClicked: () -> Unit = {},
            onSupportClicked: () -> Unit = {},
            onDocumentationClicked: () -> Unit = {},
            onSignupClicked:  () -> Unit = {},
        ): HomeScreenUiState.LoadingOrSuccess {
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

            return HomeScreenUiState.LoadingOrSuccess(
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
                        docUrl =URLs.SmartSignalsOverview.emulator
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
                            "$DETECTED_STRING. $dailyRequests per day"
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
                                "$DETECTED_STRING. Device location is $originCountry"
                            originTimezone != null ->
                                "$DETECTED_STRING. Device timezone is $originTimezone"
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
                onReload = onReload,
                onSupportClicked = onSupportClicked,
                onDocumentationClicked = onDocumentationClicked,
                onSignupClicked = onSignupClicked,
            )
        }

        private fun Boolean.detectionStatusString(): String {
            return if (this) DETECTED_STRING else NOT_DETECTED_STRING
        }

        private const val NOT_DETECTED_STRING = "Not detected"
        private const val DETECTED_STRING = "Detected"
    }
}
