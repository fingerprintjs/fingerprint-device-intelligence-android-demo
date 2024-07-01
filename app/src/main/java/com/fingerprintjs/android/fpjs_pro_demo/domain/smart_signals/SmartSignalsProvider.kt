package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import android.net.Uri
import com.fasterxml.jackson.databind.ObjectMapper
import com.fingerprintjs.android.fpjs_pro_demo.constants.Protected
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.flatten
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.flow.first
import javax.inject.Inject

typealias SmartSignalsResponse = Result<SmartSignals, SmartSignalsError>

class SmartSignalsProvider @Inject constructor(
    private val customApiKeysUseCase: CustomApiKeysUseCase,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
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
            if (baseUrl == null || origin == null)
                return Err(SmartSignalsError.EndpointInfoNotSetInApp)
            url = Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("event")
                .appendPath(requestId)
                .toString()
            headers["Origin"] = origin
        } else {
            url = Uri.parse(URLs.eventsEndpoint)
                .buildUpon()
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
                runCatching {
                    if (it.isSuccessful) {
                        val dto = objectMapper.readValue(it.body.orEmpty(), SmartSignalsDto::class.java)!!
                        Ok(dto.toSmartSignals())
                    } else {
                        val dto = objectMapper.readValue(it.body.orEmpty(), SmartSignalsErrorDto::class.java)!!
                        Err(dto.toSmartSignalsError())
                    }
                }
                    .mapError { SmartSignalsError.ParseError }
                    .flatten()
            }
    }
}
