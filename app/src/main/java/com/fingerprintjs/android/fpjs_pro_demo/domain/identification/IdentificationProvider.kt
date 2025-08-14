package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory
import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume

class IdentificationProvider @Inject constructor(
    customApiKeysUseCase: CustomApiKeysUseCase,
    @Named("networkTimeoutMillis") private val networkTimeoutMillis: Int,
    private val app: App,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val fingerprintJs = customApiKeysUseCase.state
        .map {
            FingerprintJSFactory(app).createInstance(
                Configuration(
                    apiKey = if (it.enabled) it.public else Credentials.apiKey,
                    endpointUrl = if (it.enabled) it.region.endpointUrl else Credentials.endpointUrl,
                    extendedResponseFormat = true,
                    locationTimeoutMillis = 30_000L,
                    allowUseOfLocationData = true,
                )
            )
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    suspend fun getVisitorId(): FingerprintJSProResult {
        return withContext(Dispatchers.IO) {
            fingerprintJs.first().getVisitorId()
        }
    }

    private suspend fun FingerprintJS.getVisitorId(): FingerprintJSProResult {
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
