package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprintjs.android.fpjs_pro.Failed
import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.github.michaelbull.result.Err
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class IdentificationProvider @Inject constructor(
    customApiKeysUseCase: CustomApiKeysUseCase,
    @Named("networkTimeoutMillis") private val networkTimeoutMillis: Int,
    private val app: App,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            customApiKeysUseCase.state.collectLatest { state ->
                IdentificationInstance.updateInstance(app, state)
            }
        }
    }

    suspend fun getVisitorId(): FingerprintJSProResult =
        IdentificationInstance.getVisitorId(networkTimeoutMillis) ?: Err(Failed("", ""))

    fun startGatheringDeviceIntelligence() =
        IdentificationInstance.startGatheringDeviceIntelligence()

    fun stopGatheringDeviceIntelligence() =
        IdentificationInstance.stopGatheringDeviceIntelligence()
}
