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
    suspend fun getDRN(): DRNResponse =
        identificationProvider.getVisitorId()
            .mapError { DRNError.Unknown }
            .flatMap { getDRN(it.visitorId) }

    private suspend fun getDRN(visitorId: String): DRNResponse {
        val customKeysState = customApiKeysUseCase.state.first()
        val headers = mutableMapOf(
            "Accept" to "application/json"
        )
        val url: String
        if (!customKeysState.enabled) {
            return Err(DRNError.EndpointInfoNotSetInApp)
        } else {
            url = URL.format(visitorId).toUri().toString()
            headers["Authorization"] = "Bearer ${customKeysState.secret}"
            headers["X-API-Version"] = "2024-09-01"
        }

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
