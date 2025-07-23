package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import android.net.Uri
import com.fingerprintjs.android.fpjs_pro_demo.constants.Protected
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SmartSignalsProvider @Inject constructor(
    private val customApiKeysUseCase: CustomApiKeysUseCase,
    private val httpClient: HttpClient,
    private val smartSignalsBodyParser: SmartSignalsBodyParser,
) {

    suspend fun getSmartSignals(
        requestId: String,
    ): SmartSignalsResponse {
        val customKeysState = customApiKeysUseCase.state.first()
        val headers = mutableMapOf(
            "Accept" to "application/json"
        )
        val url: String
        if (!customKeysState.enabled) {
            val baseUrl = Protected.smartSignalsBaseUrl
            val origin = Protected.smartSignalsOrigin
            if (baseUrl == null || origin == null) {
                return Err(SmartSignalsError.EndpointInfoNotSetInApp)
            }
            url = Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("event")
                .appendPath(requestId)
                .toString()
            headers["Origin"] = origin
        } else {
            url = Uri.parse(customKeysState.region.endpointUrl)
                .buildUpon()
                .appendPath("events")
                .appendPath(requestId)
                .toString()
            headers["Auth-API-Key"] = customKeysState.secret
        }

        return httpClient.request(
            url = url,
            headers = headers,
        )
            .mapError {
                when (it) {
                    is HttpClient.Error.IO -> SmartSignalsError.NetworkError(cause = it.cause)
                    is HttpClient.Error.Unknown -> SmartSignalsError.Unknown
                }
            }
            .andThen {
                if (it.isSuccessful) {
                    smartSignalsBodyParser.parseSmartSignals(it.body)
                        .mapError { SmartSignalsError.ParseError }
                } else {
                    smartSignalsBodyParser.parseSmartSignalsError(it.body).fold(
                        success = { Err(it) },
                        failure = { Err(SmartSignalsError.ParseError) }
                    )
                }
            }
    }
}
