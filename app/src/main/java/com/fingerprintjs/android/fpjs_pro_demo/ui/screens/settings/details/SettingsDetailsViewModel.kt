package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysState
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsDetailsViewModel @Inject constructor(
    private val customApiKeysUseCase: CustomApiKeysUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<SettingsDetailsUiState?>(null)
    val state = _state.asStateFlow()

    private val _backAction = MutableSharedFlow<Unit>()
    val backAction = _backAction.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SettingsDetailsUiState(
                customApiKeysState = customApiKeysUseCase.state.first(),
                onPublicChanged = { updateCustomKeysState { copy(public = it) } },
                onSecretChanged = { updateCustomKeysState { copy(secret = it) } },
                onRegionChanged = { updateCustomKeysState { copy(region = it) } },
                onEnabledChanged = { updateCustomKeysState { copy(enabled = it) } },
                onLeave = ::onLeave,
                validationPromptState = null,
            )
        }
    }

    private fun updateCustomKeysState(transform: (CustomApiKeysState.() -> CustomApiKeysState)) {
        _state.update { it?.copy(customApiKeysState = it.customApiKeysState.transform()) }
    }

    private fun updateValidationPromptState(
        transform: SettingsDetailsUiState.ValidationPromptState?.() -> SettingsDetailsUiState.ValidationPromptState?
    ) {
        _state.update { it?.copy(validationPromptState = it.validationPromptState.transform()) }
    }

    private suspend fun onLeave() {
        val currentState = _state.value?.customApiKeysState ?: return
        if (customApiKeysUseCase.tryUpdate(currentState)) {
            leave()
        } else {
            updateValidationPromptState {
                SettingsDetailsUiState.ValidationPromptState.InvalidKeysState(
                    onCancel = { updateValidationPromptState { null } },
                    onContinue = {
                        updateValidationPromptState { null }
                        leave()
                    },
                )
            }
        }
    }

    private suspend fun leave() = _backAction.emit(Unit)
}
