package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysState

data class SettingsDetailsUiState(
    val customApiKeysState: CustomApiKeysState,
    val onPublicChanged: (String) -> Unit,
    val onSecretChanged: (String) -> Unit,
    val onRegionChanged: (Configuration.Region) -> Unit,
    val onEnabledChanged: (Boolean) -> Unit,
    val onLeave: suspend () -> Unit,
    val validationPromptState: ValidationPromptState?,
) {
    data class ValidationPromptState(
        val title: String,
        val description: String,
        val cancelButtonTitle: String,
        val continueButtonTitle: String,
        val onCancel: () -> Unit,
        val onContinue: suspend () -> Unit,
    ) {
        companion object {
            fun InvalidKeysState(
                onCancel: () -> Unit,
                onContinue: suspend () -> Unit,
            ) = ValidationPromptState(
                title = "Invalid keys!",
                description = "Your changes will not be saved. The app will continue to use the previously saved API keys.",
                cancelButtonTitle = "Continue editing",
                continueButtonTitle = "Discard changes",
                onCancel = onCancel,
                onContinue = onContinue,
            )
        }
    }
}
