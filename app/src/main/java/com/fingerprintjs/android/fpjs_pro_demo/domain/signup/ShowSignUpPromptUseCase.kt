package com.fingerprintjs.android.fpjs_pro_demo.domain.signup

import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ShowSignUpPromptUseCase @Inject constructor(
    private val allowSignUpPromptUseCase: AllowSignUpPromptUseCase,
    private val customApiKeysUseCase: CustomApiKeysUseCase,
) {
    val showAllowed: Flow<Boolean>
        get() = allowSignUpPromptUseCase.showAllowed
            .combine(customApiKeysUseCase.state) { allowed, apiKeysEnabled ->
                allowed && !apiKeysEnabled.enabled
            }

    suspend fun initialize() = allowSignUpPromptUseCase.updateState()

    suspend fun onFingerprintSuccess() = allowSignUpPromptUseCase.onFingerprintSuccess()

    suspend fun onHideRequested() = allowSignUpPromptUseCase.onHideRequested()
}
