package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage
import com.github.michaelbull.result.get
import junit.framework.TestCase
import org.junit.Test

class SmartSignalsBodyParserUnitTests {
    private val parser = CommonComponentStorage.commonComponent.smartSignalsBodyParser()

    @Test
    fun givenFlatBooleanSignal_whenParse_thenSuccess() {
        val signals = parser.parseSmartSignals("""{ "emulator": true }""").get()!!
        TestCase.assertTrue(signals.emulator is SmartSignalInfo.Success)
        val success = signals.emulator as SmartSignalInfo.Success
        TestCase.assertEquals(SmartSignal.Emulator(result = true), success.typedData)
        TestCase.assertEquals("emulator", success.rawKey)
    }

    @Test
    fun givenFlatBooleanWithUnexpectedType_whenParse_thenParseError() {
        val signals = parser.parseSmartSignals("""{ "emulator": "not-a-bool" }""").get()!!
        TestCase.assertTrue(signals.emulator is SmartSignalInfo.ParseError)
    }

    @Test
    fun givenSignalMissing_whenParse_thenDisabled() {
        val signals = parser.parseSmartSignals("""{ }""").get()!!
        TestCase.assertTrue(signals.emulator is SmartSignalInfo.Disabled)
        TestCase.assertTrue(signals.developerTools is SmartSignalInfo.Disabled)
        TestCase.assertTrue(signals.ipInfo is SmartSignalInfo.Disabled)
    }

    @Test
    fun givenExtraRootFields_whenParse_thenIgnoredAndKnownSignalsParsed() {
        val body = """
        {
            "developer_tools": true,
            "unknown_extra_field": 42,
            "some_new_v4_signal": { "result": true }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        TestCase.assertTrue(signals.developerTools is SmartSignalInfo.Success)
    }

    @Test
    fun givenFactoryResetTimestamp_whenParse_thenSuccessWithDerivedTime() {
        val signals = parser.parseSmartSignals("""{ "factory_reset_timestamp": 1707144876 }""").get()!!
        TestCase.assertTrue(signals.factoryReset is SmartSignalInfo.Success)
        val success = signals.factoryReset as SmartSignalInfo.Success
        TestCase.assertEquals(1707144876L, success.typedData.timestamp)
        TestCase.assertEquals("2024-02-05T14:54:36Z", success.typedData.time)
    }

    @Test
    fun givenFactoryResetMissing_whenParse_thenDisabled() {
        val signals = parser.parseSmartSignals("""{ }""").get()!!
        TestCase.assertTrue(signals.factoryReset is SmartSignalInfo.Disabled)
    }

    @Test
    fun givenHighActivity_whenParse_thenDailyRequestsIsNullBecauseV4OmitsIt() {
        val signals = parser.parseSmartSignals("""{ "high_activity_device": true }""").get()!!
        val success = signals.highActivity as SmartSignalInfo.Success
        TestCase.assertEquals(true, success.typedData.result)
        TestCase.assertNull(success.typedData.dailyRequests)
    }

    @Test
    fun givenVpnWithAllFields_whenParse_thenSplitFieldsAssembled() {
        val body = """
        {
            "vpn": true,
            "vpn_confidence": "high",
            "vpn_origin_timezone": "America/New_York",
            "vpn_origin_country": "DE",
            "vpn_methods": {
                "timezone_mismatch": true,
                "public_vpn": false,
                "auxiliary_mobile": false,
                "relay": true
            }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val success = signals.vpn as SmartSignalInfo.Success
        val vpn = success.typedData
        TestCase.assertEquals(true, vpn.result)
        TestCase.assertEquals("high", vpn.confidence)
        TestCase.assertEquals("America/New_York", vpn.originTimezone)
        TestCase.assertEquals("DE", vpn.originCountry)
        TestCase.assertEquals(
            mapOf(
                "timezone_mismatch" to true,
                "public_vpn" to false,
                "auxiliary_mobile" to false,
                "relay" to true,
            ),
            vpn.methods,
        )
    }

    @Test
    fun givenVpnOriginUnknown_whenParse_thenOriginCoercedToNull() {
        val body = """
        {
            "vpn": false,
            "vpn_origin_country": "unknown",
            "vpn_origin_timezone": ""
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val vpn = (signals.vpn as SmartSignalInfo.Success).typedData
        TestCase.assertNull(vpn.originCountry)
        TestCase.assertNull(vpn.originTimezone)
    }

    @Test
    fun givenTampering_whenParse_thenAnomalyScoreReadFromDetails() {
        val body = """
        {
            "tampering": true,
            "tampering_details": { "anomaly_score": 1.5 }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val tampering = (signals.tampering as SmartSignalInfo.Success).typedData
        TestCase.assertEquals(true, tampering.result)
        TestCase.assertEquals(1.5f, tampering.anomalyScore)
    }

    @Test
    fun givenTamperingWithoutDetails_whenParse_thenAnomalyScoreDefaultsToZero() {
        val signals = parser.parseSmartSignals("""{ "tampering": false }""").get()!!
        val tampering = (signals.tampering as SmartSignalInfo.Success).typedData
        TestCase.assertEquals(false, tampering.result)
        TestCase.assertEquals(0f, tampering.anomalyScore)
    }

    @Test
    fun givenProxyWithDetails_whenParse_thenDetailsMappedToV3CamelCaseKeys() {
        val body = """
        {
            "proxy": true,
            "proxy_confidence": "medium",
            "proxy_details": {
                "proxy_type": "residential",
                "last_seen_at": 1779321600000
            }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val proxy = (signals.proxy as SmartSignalInfo.Success).typedData
        TestCase.assertEquals(true, proxy.result)
        TestCase.assertEquals("medium", proxy.confidence)
        TestCase.assertEquals("residential", proxy.details["proxy_type"])
        TestCase.assertEquals("2026-05-21T00:00:00Z", proxy.details["last_seen_at"])
    }

    @Test
    fun givenIpBlocklist_whenParse_thenResultDerivedFromAnyTrueFlag() {
        val body = """
        {
            "ip_blocklist": {
                "email_spam": false,
                "attack_source": true,
                "tor_node": false
            }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val ipBlocklist = (signals.ipBlocklist as SmartSignalInfo.Success).typedData
        TestCase.assertEquals(true, ipBlocklist.result)
        TestCase.assertEquals(
            mapOf("email_spam" to false, "attack_source" to true),
            ipBlocklist.details,
        )
    }

    @Test
    fun givenIpInfo_whenParse_thenNestedStructureBuilt() {
        val body = """
        {
            "ip_info": {
                "v4": {
                    "address": "106.222.216.201",
                    "geolocation": {
                        "accuracy_radius": 500,
                        "latitude": 23.25469,
                        "longitude": 77.40289,
                        "postal_code": "464993",
                        "timezone": "Asia/Kolkata",
                        "city_name": "Bhopal",
                        "country_code": "IN",
                        "country_name": "India",
                        "continent_code": "AS",
                        "continent_name": "Asia"
                    },
                    "asn": "24560",
                    "asn_name": "Bharti Airtel",
                    "asn_network": "106.222.208.0/20",
                    "asn_type": "isp",
                    "datacenter_result": false
                }
            }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val ipInfo = (signals.ipInfo as SmartSignalInfo.Success).typedData
        TestCase.assertEquals("106.222.216.201", ipInfo.v4.address)
        TestCase.assertEquals("24560", ipInfo.v4.asn.asn)
        TestCase.assertEquals("Bharti Airtel", ipInfo.v4.asn.name)
        TestCase.assertEquals("106.222.208.0/20", ipInfo.v4.asn.network)
        TestCase.assertEquals(false, ipInfo.v4.datacenter.result)
        TestCase.assertEquals("", ipInfo.v4.datacenter.name)
        TestCase.assertEquals(464993, ipInfo.v4.geolocation.postalCode)
        TestCase.assertEquals("Bhopal", ipInfo.v4.geolocation.city.name)
        TestCase.assertEquals("IN", ipInfo.v4.geolocation.country.code)
        TestCase.assertEquals("Asia", ipInfo.v4.geolocation.continent.name)
    }

    @Test
    fun givenProximityWithAllFields_whenParse_thenAllFieldsExtracted() {
        val body = """
        {
            "proximity": {
                "id": "VhRLfKfgXDt",
                "precision_radius": 10,
                "confidence": 0.76
            }
        }
        """.trimIndent()
        val signals = parser.parseSmartSignals(body).get()!!
        val proximity = (signals.proximity as SmartSignalInfo.Success).typedData
        TestCase.assertEquals("VhRLfKfgXDt", proximity.id)
        TestCase.assertEquals(10, proximity.precisionRadius)
        TestCase.assertEquals(0.76f, proximity.confidence)
    }

    @Test
    fun givenProximityEmpty_whenParse_thenAllFieldsNull() {
        val signals = parser.parseSmartSignals("""{ "proximity": {} }""").get()!!
        val proximity = (signals.proximity as SmartSignalInfo.Success).typedData
        TestCase.assertNull(proximity.id)
        TestCase.assertNull(proximity.precisionRadius)
        TestCase.assertNull(proximity.confidence)
    }

    @Test
    fun givenValidErrorBody_whenParseSmartSignalsError_thenCorrectErrorReturned() {
        val result = parser.parseSmartSignalsError(
            """
            {
                "error": {
                    "message": "some_value",
                    "code": "TokenRequired"
                }
            }
            """.trimIndent()
        )
        TestCase.assertEquals(SmartSignalsError.TokenRequired, result.get())
    }

    @Test
    fun givenInvalidErrorBody_whenParseSmartSignalsError_thenErrorReturned() {
        val result = parser.parseSmartSignalsError("whatever_string")
        TestCase.assertTrue(result.isErr)
    }
}
