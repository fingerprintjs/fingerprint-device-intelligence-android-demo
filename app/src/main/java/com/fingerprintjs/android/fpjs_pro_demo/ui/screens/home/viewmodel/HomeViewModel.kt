package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingerprintjs.android.fpjs_pro_demo.BuildConfig
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.FingerprintJSProResult
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.IdentificationProvider
import com.fingerprintjs.android.fpjs_pro_demo.domain.signup.ShowSignUpPromptUseCase
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsError
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsProvider
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsResponse
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.FingerprintData.Loading
import com.fingerprintjs.android.fpjs_pro_demo.utils.stateMocks
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val identificationProvider: IdentificationProvider,
    private val showSignUpPromptUseCase: ShowSignUpPromptUseCase,
    private val smartSignalsProvider: SmartSignalsProvider,
    private val homeScreenUiStateCreator: HomeScreenUiStateCreator,
) : ViewModel() {
    private val externalLinkToOpenMutable = MutableSharedFlow<String>()
    val externalLinkToOpen: Flow<String>
        get() = externalLinkToOpenMutable

    private val textToCopyMutable = MutableSharedFlow<String>()
    val textToCopy: Flow<String>
        get() = textToCopyMutable

    private val goToApiKeySettingsMutable = MutableSharedFlow<Unit>()
    val goToApiKeySettings: Flow<Unit>
        get() = goToApiKeySettingsMutable

    private val fingerprintData = MutableSharedFlow<FingerprintData>(1)

    private val mockingState = MutableStateFlow(
        HomeScreenUiState.MockingState(
            enabled = false,
            onToggle = ::onToggleMocking,
        ).takeIf { BuildConfig.ALLOW_MOCKS }
    )

    private val contentState: StateFlow<HomeScreenUiState.Content> =
        fingerprintData
            .map {
                val data: FingerprintData.Data = when (it) {
                    is Loading -> {
                        // todo (minor): consider using the latest loaded data to minimize UI transitions between loading and success states
                        FingerprintData.Data(
                            fingerprintSdkResponse = Ok(stateMocks.fingerprintJSResponse)
                        )
                    }
                    is FingerprintData.Data -> it
                }

                with(homeScreenUiStateCreator) {
                    HomeScreenUiState.Content.create(
                        fingerprintSdkResponse = data.fingerprintSdkResponse,
                        smartSignalsData = data.smartSignalsResponse,
                        isLoading = it is Loading,
                        onSmartSignalDocClicked = ::onLaunchUrl,
                        onHideSignupPrompt = ::onHideSignupPromptClicked,
                        onPutToClipboard = ::onPutToClipboard,
                        onSignupPromptClicked = ::onSignupClicked,
                        onReload = ::onReload,
                        onSupportClicked = ::onSupportClicked,
                        onGotoApiKeysSettings = ::onGotoApiKeysSettings,
                    )
                }
            }
            .onEach {
                if (it is HomeScreenUiState.Content.LoadingOrSuccess && !it.isLoading) {
                    showSignUpPromptUseCase.onFingerprintSuccess()
                }
            }
            .combine(showSignUpPromptUseCase.showAllowed) { uiState, showAllowed ->
                if (uiState is HomeScreenUiState.Content.LoadingOrSuccess && !uiState.isLoading) {
                    uiState.copy(isSignupPromptShown = showAllowed)
                } else {
                    uiState
                }
            }
            .stateIn(
                scope = viewModelScope + Dispatchers.IO,
                started = SharingStarted.Eagerly,
                initialValue = HomeScreenUiState.Content.TapToBegin(
                    onTap = ::onReload,
                ),
            )

    val state = combine(contentState, mockingState) { content, mocking ->
        HomeScreenUiState(
            appBar = HomeScreenUiState.AppBarState(
                onSupportClicked = ::onSupportClicked,
                onDocumentationClicked = ::onDocumentationClicked,
                onSignupClicked = ::onSignupClicked
            ),
            reload = HomeScreenUiState.ReloadState(
                reloadAllowed = when (content) {
                    is HomeScreenUiState.Content.Error -> true
                    is HomeScreenUiState.Content.LoadingOrSuccess -> !content.isLoading
                    is HomeScreenUiState.Content.TapToBegin -> false
                },
                onReload = ::onReload,
            ),
            mocking = mocking,
            content = content,
        )
    }.shareIn(viewModelScope + Dispatchers.IO, started = SharingStarted.Eagerly, replay = 1)

    private fun onToggleMocking(): Job = viewModelScope.launch {
        mockingState.update { it?.copy(enabled = !it.enabled) }
    }

    private fun onReload() = viewModelScope.launch {
        fingerprintData.emit(Loading)
        val response = getRealOrStubbedFingerprintData()
        fingerprintData.emit(FingerprintData.Data(response))
        val smartSignals = getSmartSignalData(response)
        fingerprintData.emit(FingerprintData.Data(response, SmartSignalsData.Data(smartSignals)))
    }

    private fun onSupportClicked() = onLaunchUrl(URLs.support)
    private fun onDocumentationClicked() = onLaunchUrl(URLs.documentation)
    private fun onSignupClicked() = onLaunchUrl(URLs.signup)

    private fun onHideSignupPromptClicked() = viewModelScope.launch { showSignUpPromptUseCase.onHideRequested() }
    private fun onLaunchUrl(url: String) = viewModelScope.launch { externalLinkToOpenMutable.emit(url) }
    private fun onPutToClipboard(text: String) = viewModelScope.launch { textToCopyMutable.emit(text) }
    private fun onGotoApiKeysSettings() = viewModelScope.launch { goToApiKeySettingsMutable.emit(Unit) }

    private var reloadCount = 0
    private suspend fun getRealOrStubbedFingerprintData(): FingerprintJSProResult {
        return withContext(Dispatchers.IO) {
            if (mockingState.value?.enabled == true) {
                delay(2000)
                if (++reloadCount % 4 == 0) {
                    Err(stateMocks.fingerprintJSOtherError)
                } else {
                    Ok(stateMocks.fingerprintJSResponse)
                }
            } else
                identificationProvider.getVisitorId()
            
        }
    }

    private suspend fun getSmartSignalData(fingerprintSdkResponse: FingerprintJSProResult): SmartSignalsResponse {
        return withContext(Dispatchers.IO) {
            if (mockingState.value?.enabled == true) {
                delay(2000)
                if (++reloadCount % 4 == 0) {
                    Err(SmartSignalsError.Unknown)
                } else {
                    Ok(stateMocks.smartSignals)
                }
            } else {
                fingerprintSdkResponse
                    // if fingerprintSdkResponse is error, consider smart signals response as
                    // error too for simplicity
                    .mapError { SmartSignalsError.Unknown }
                    .flatMap { smartSignalsProvider.getSmartSignals(it.requestId) }
            }
        }
    }
}

private sealed class FingerprintData {
    data object Loading : FingerprintData()
    data class Data(
        val fingerprintSdkResponse: FingerprintJSProResult,
        val smartSignalsResponse: SmartSignalsData = SmartSignalsData.Loading,
    ) : FingerprintData()
}

sealed class SmartSignalsData {
    data object Loading : SmartSignalsData()
    data class Data(
        val smartSignalsResponse: SmartSignalsResponse,
    ) : SmartSignalsData()
}
