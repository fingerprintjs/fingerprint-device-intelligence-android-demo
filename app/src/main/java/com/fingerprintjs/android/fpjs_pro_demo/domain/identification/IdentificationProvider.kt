package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintException
import com.fingerprintjs.android.fpjs_pro.FingerprintFactory
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
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Named

data class VisitorIdResponse(
    val result: FingerprintJSProResult,
    val secret: String,
)

class IdentificationProvider @Inject constructor(
    customApiKeysUseCase: CustomApiKeysUseCase,
    @Named("networkTimeoutMillis") private val networkTimeoutMillis: Int,
    private val app: App,
    private val scope: CoroutineScope,
) {
    private val secureRandom = SecureRandom()

    private val fingerprintJs = customApiKeysUseCase.state
        .map {
            FingerprintFactory(app).createInstance(
                Configuration(
                    apiKey = if (it.enabled) it.public else Credentials.apiKey,
                    endpointUrl = if (it.enabled) it.region.endpointUrl else Credentials.endpointUrl,
                    allowUseOfLocationData = true,
                )
            )
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    suspend fun getVisitorId(): VisitorIdResponse {
        val secret = "%08x".format(secureRandom.nextInt())
        val result = withContext(Dispatchers.IO) {
            try {
                Ok(fingerprintJs.first().getVisitorId(networkTimeoutMillis, mapOf<String, Any>("secret" to secret)))
            } catch (e: FingerprintException) {
                Err(e.error)
            }
        }
        return VisitorIdResponse(result, secret)
    }
}
