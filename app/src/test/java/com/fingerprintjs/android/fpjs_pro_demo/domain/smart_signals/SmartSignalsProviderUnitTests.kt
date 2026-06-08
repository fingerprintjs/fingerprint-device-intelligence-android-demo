package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import junit.framework.TestCase
import org.junit.Test

class SmartSignalsProviderUnitTests {

    @Test
    fun buildProxyRequest_appendsSecretAsQueryParameter() {
        val request = buildProxyRequest(
            baseUrl = "https://proxy.example.com",
            requestId = "1111111111111.AAAAAA",
            secret = "d1f2e879",
            username = "user",
            password = "pass",
        )

        TestCase.assertEquals(
            "https://proxy.example.com/event/v4/1111111111111.AAAAAA?secret=d1f2e879",
            request.url,
        )
    }

    @Test
    fun buildProxyRequest_setsBasicAuthorizationHeaderWithBase64EncodedCredentials() {
        val request = buildProxyRequest(
            baseUrl = "https://proxy.example.com",
            requestId = "req",
            secret = "abcdef01",
            username = "user",
            password = "pass",
        )

        TestCase.assertEquals("Basic dXNlcjpwYXNz", request.headers["Authorization"])
    }

    @Test
    fun buildProxyRequest_setsAcceptHeader() {
        val request = buildProxyRequest(
            baseUrl = "https://proxy.example.com",
            requestId = "req",
            secret = "abcdef01",
            username = "u",
            password = "p",
        )

        TestCase.assertEquals("application/json", request.headers["Accept"])
    }

    @Test
    fun buildProxyRequest_trailingSlashInBaseUrlDoesNotProduceDoubleSlash() {
        val request = buildProxyRequest(
            baseUrl = "https://proxy.example.com/",
            requestId = "req",
            secret = "00000007",
            username = "u",
            password = "p",
        )

        TestCase.assertEquals(
            "https://proxy.example.com/event/v4/req?secret=00000007",
            request.url,
        )
    }

    @Test
    fun buildProxyRequest_differentSecretsProduceDifferentUrls() {
        val r1 = buildProxyRequest(
            baseUrl = "https://proxy.example.com",
            requestId = "req",
            secret = "00000001",
            username = "u",
            password = "p",
        )
        val r2 = buildProxyRequest(
            baseUrl = "https://proxy.example.com",
            requestId = "req",
            secret = "00000002",
            username = "u",
            password = "p",
        )

        TestCase.assertFalse(r1.url == r2.url)
    }

    @Test
    fun buildCustomKeysRequest_buildsV4EventsPathAndSetsBearerAuthorizationHeader() {
        val request = buildCustomKeysRequest(
            endpointUrl = "https://api.fpjs.io",
            requestId = "1111111111111.AAAAAA",
            apiKey = "secret-api-key",
        )

        TestCase.assertEquals(
            "https://api.fpjs.io/v4/events/1111111111111.AAAAAA",
            request.url,
        )
        TestCase.assertEquals("Bearer secret-api-key", request.headers["Authorization"])
        TestCase.assertEquals("application/json", request.headers["Accept"])
    }

    @Test
    fun buildCustomKeysRequest_doesNotIncludeProxyOriginHeaderOrSecretParam() {
        val request = buildCustomKeysRequest(
            endpointUrl = "https://api.fpjs.io",
            requestId = "req",
            apiKey = "key",
        )

        TestCase.assertFalse(request.headers.containsKey("Origin"))
        TestCase.assertFalse(request.headers.containsKey("Auth-API-Key"))
        TestCase.assertFalse(request.url.contains("secret="))
    }
}
