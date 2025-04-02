package com.fingerprintjs.android.fpjs_pro_demo.domain.drn

import androidx.core.net.toUri
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.IdentificationProvider
import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DRNProvider @Inject constructor(
    private val httpClient: HttpClient,
    private val customApiKeysUseCase: CustomApiKeysUseCase,
    private val identificationProvider: IdentificationProvider,
) {
    suspend fun getDRN(): DRNResponse {
        val keys = customApiKeysUseCase.state.first()
        return if (!keys.enabled) {
            Err(DRNError.EndpointInfoNotSetInApp)
        } else {
            identificationProvider.getVisitorId()
                .mapError { DRNError.Unknown }
                .flatMap { getDRN(it.visitorId, keys.secret) }
        }
    }

    private suspend fun getDRN(visitorId: String, secret: String): DRNResponse {
        val headers = mutableMapOf<String, String>()
        val url = URL.format(visitorId).toUri().toString()
        headers["Authorization"] = "Bearer $secret"
        headers["X-API-Version"] = "2024-09-01"

        return httpClient.request(
            url = url,
            headers = headers,
        ).parseDRN()
    }

    companion object {
        private const val URL =
            "https://drn-api.fpjs.io/drn/%s?signals=regional_activity,suspect_score,timestamps"
    }
}
