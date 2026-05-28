package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.fingerprintjs.android.fpjs_pro_demo.constants.Protected
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.first
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.ByteString.Companion.encodeUtf8
import javax.inject.Inject

internal data class SmartSignalsRequest(
    val url: String,
    val headers: Map<String, String>,
)
internal fun buildProxyRequest(
    baseUrl: String,
    requestId: String,
    secret: String,
    username: String,
    password: String,
): SmartSignalsRequest {
    val url = baseUrl.toHttpUrl().newBuilder()
        .addPathSegment("event")
        .addPathSegment("v4")
        .addPathSegment(requestId)
        .addQueryParameter("secret", secret)
        .build()
        .toString()
    val basicAuth = "$username:$password".encodeUtf8().base64()
    return SmartSignalsRequest(
        url = url,
        headers = mapOf(
            "Accept" to "application/json",
            "Authorization" to "Basic $basicAuth",
        ),
    )
}

internal fun buildCustomKeysRequest(
    endpointUrl: String,
    requestId: String,
    apiKey: String,
): SmartSignalsRequest {
    val url = endpointUrl.toHttpUrl().newBuilder()
        .addPathSegment("v4")
        .addPathSegment("events")
        .addPathSegment(requestId)
        .build()
        .toString()
    return SmartSignalsRequest(
        url = url,
        headers = mapOf(
            "Accept" to "application/json",
            "Authorization" to "Bearer $apiKey",
        ),
    )
}

class SmartSignalsProvider @Inject constructor(
    private val customApiKeysUseCase: CustomApiKeysUseCase,
    private val httpClient: HttpClient,
    private val smartSignalsBodyParser: SmartSignalsBodyParser,
) {

    @Suppress("ReturnCount")
    suspend fun getSmartSignals(
        requestId: String,
        secret: String,
    ): SmartSignalsResponse {
        val customKeysState = customApiKeysUseCase.state.first()
        val request: SmartSignalsRequest = if (!customKeysState.enabled) {
            val baseUrl = Protected.smartSignalsBaseUrl
            val origin = Protected.smartSignalsOrigin
            if (baseUrl == null || origin == null) {
                return Err(SmartSignalsError.EndpointInfoNotSetInApp)
            }
            val username = Protected.username
            val password = Protected.password
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                return Err(SmartSignalsError.BasicAuthCredentialsNotSetInApp)
            }
            buildProxyRequest(
                baseUrl = baseUrl,
                requestId = requestId,
                secret = secret,
                username = username,
                password = password,
            )
        } else {
            buildCustomKeysRequest(
                endpointUrl = customKeysState.region.endpointUrl,
                requestId = requestId,
                apiKey = customKeysState.secret,
            )
        }

        return httpClient.request(
            url = request.url,
            headers = request.headers,
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
