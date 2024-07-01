package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    customApiKeysUseCase: CustomApiKeysUseCase,
) : ViewModel() {
    val state = customApiKeysUseCase
        .state
        .map {
            SettingsUiState(
                customKeysEnabled = it.enabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsUiState(customKeysEnabled = false)
        )
}
