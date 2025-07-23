package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory
import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysState
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume

object IdentificationInstance {
    private val fingerprint: AtomicReference<FingerprintJS?> = AtomicReference(null)
    private val dataCollectionAllowed: AtomicReference<Boolean?> = AtomicReference(null)

    fun updateInstance(app: App, keys: CustomApiKeysState) {
        stopGatheringDeviceIntelligence()

        val config = Configuration(
            apiKey = if (keys.enabled) keys.public else Credentials.apiKey,
            endpointUrl = if (keys.enabled) keys.region.endpointUrl else Credentials.endpointUrl,
            extendedResponseFormat = true,
            allowUseOfLocationData = true
        )
        val instance = FingerprintJSFactory(app).createInstance(config)
        fingerprint.set(instance)

        if (dataCollectionAllowed.get() == true) {
            startGatheringDeviceIntelligence()
        }
    }

    suspend fun getVisitorId(networkTimeoutMillis: Int): FingerprintJSProResult? {
        return withContext(Dispatchers.IO) {
            fingerprint.get()?.getVisitorId(networkTimeoutMillis)
        }
    }

    fun startGatheringDeviceIntelligence() {
        fingerprint.get()?.startGatheringDeviceIntelligence()
        dataCollectionAllowed.set(true)
    }

    fun stopGatheringDeviceIntelligence() {
        fingerprint.get()?.stopGatheringDeviceIntelligence()
    }

    private suspend fun FingerprintJS.getVisitorId(networkTimeoutMillis: Int): FingerprintJSProResult {
        return suspendCancellableCoroutine { cancellableContinuation ->
            this.getVisitorId(
                timeoutMillis = networkTimeoutMillis,
                listener = {
                    cancellableContinuation.resume(
                        Ok(it)
                    )
                },
                errorListener = {
                    cancellableContinuation.resume(
                        Err(it)
                    )
                },
            )
        }
    }
}
