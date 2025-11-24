package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import com.fingerprintjs.android.fpjs_pro_demo.utils.stateMocks
import junit.framework.TestCase
import org.junit.Test

class HomeScreenUiStateCreatorUnitTests {
    private val creator = CommonComponentStorage.commonComponent.homeScreenUiStateCreator()

    @Test
    fun givenFpjsResponseAndSmartSignal_whenCreateRawJson_thenExpectedJsonReturned() {
        // given
        val fpjsResponse = stateMocks.fingerprintJSResponse
        val smartSignals = stateMocks.smartSignals

        // when
        val json = creator.createRawJson(fpjsResponse, smartSignals)

        // then
        TestCase.assertEquals(expectedJson, json)
    }

    // Tests for getVpnStatusString
    @Test
    fun givenVpnWithResultFalse_whenGetVpnStatusString_thenReturnsNotDetected() {
        // given
        val vpn = SmartSignal.Vpn(
            result = false,
            methods = emptyMap(),
            confidence = null,
            originCountry = null
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertEquals("Not detected", result)
    }

    @Test
    fun givenVpnWithPublicVpnMethod_whenGetVpnStatusString_thenReturnsDetectedWithMethod() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("publicVPN" to true),
            confidence = "high",
            originCountry = "US"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Public VPN"))
        TestCase.assertTrue(result.contains("Confidence: high"))
        TestCase.assertTrue(result.contains("Origin Country: United States"))
    }

    @Test
    fun givenVpnWithTimezoneMismatchMethod_whenGetVpnStatusString_thenReturnsDetectedWithMethod() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("timezoneMismatch" to true),
            confidence = "medium",
            originCountry = "DE"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Timezone mismatch"))
        TestCase.assertTrue(result.contains("Confidence: medium"))
        TestCase.assertTrue(result.contains("Origin Country: Germany"))
    }

    @Test
    fun givenVpnWithRelayMethod_whenGetVpnStatusString_thenReturnsDetectedWithMethod() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("relay" to true),
            confidence = "low",
            originCountry = "FR"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Relay"))
        TestCase.assertTrue(result.contains("Confidence: low"))
        TestCase.assertTrue(result.contains("Origin Country: France"))
    }

    @Test
    fun givenVpnWithAuxiliaryMobileMethod_whenGetVpnStatusString_thenReturnsDetectedWithMethod() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("auxiliaryMobile" to true),
            confidence = null,
            originCountry = null
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Auxiliary mobile"))
        TestCase.assertFalse(result.contains("Confidence:"))
        TestCase.assertFalse(result.contains("Origin Country:"))
    }

    @Test
    fun givenVpnWithMultipleMethods_whenGetVpnStatusString_thenReturnsHighestPriorityMethod() {
        // given - publicVPN has highest priority
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf(
                "publicVPN" to true,
                "timezoneMismatch" to true,
                "relay" to true
            ),
            confidence = "high",
            originCountry = "GB"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Public VPN"))
        TestCase.assertFalse(result.contains("Timezone mismatch"))
        TestCase.assertFalse(result.contains("Relay"))
    }

    @Test
    fun givenVpnWithoutConfidence_whenGetVpnStatusString_thenReturnsWithoutConfidence() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("publicVPN" to true),
            confidence = null,
            originCountry = "US"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertFalse(result.contains("Confidence:"))
        TestCase.assertTrue(result.contains("Origin Country:"))
    }

    @Test
    fun givenVpnWithoutOriginCountry_whenGetVpnStatusString_thenReturnsWithoutCountry() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("publicVPN" to true),
            confidence = "high",
            originCountry = null
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Confidence: high"))
        TestCase.assertFalse(result.contains("Origin Country:"))
    }

    @Test
    fun givenVpnWithEmptyMethods_whenGetVpnStatusString_thenReturnsDetectedWithoutMethod() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = emptyMap(),
            confidence = "high",
            originCountry = "US"
        )

        // when
        val result = with(creator) { vpn.getVpnStatusString() }

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertFalse(result.contains("("))
        TestCase.assertTrue(result.contains("Confidence: high"))
    }

    // Tests for getVpnNoteString
    @Test
    fun givenVpnWithResultFalse_whenGetVpnNoteString_thenReturnsEmptyString() {
        // given
        val vpn = SmartSignal.Vpn(
            result = false,
            methods = emptyMap(),
            confidence = null,
            originCountry = null
        )

        // when
        val result = with(creator) { vpn.getVpnNoteString() }

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenVpnWithResultTrue_whenGetVpnNoteString_thenReturnsNoteString() {
        // given
        val vpn = SmartSignal.Vpn(
            result = true,
            methods = mapOf("publicVPN" to true),
            confidence = "high",
            originCountry = "US"
        )

        // when
        val result = with(creator) { vpn.getVpnNoteString() }

        // then
        TestCase.assertEquals("Note: works without location permissions", result)
    }

    // Tests for getVpnDetectionDetails
    @Test
    fun givenEmptyMethods_whenGetVpnDetectionDetails_thenReturnsDetectedWithoutMethod() {
        // when
        val result = creator.getVpnDetectionDetails(
            methods = emptyMap(),
            confidence = "high",
            originCountry = "US"
        )

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertFalse(result.contains("("))
        TestCase.assertTrue(result.contains("Confidence: high"))
        TestCase.assertTrue(result.contains("Origin Country: United States"))
    }

    @Test
    fun givenMethodsWithPublicVpn_whenGetVpnDetectionDetails_thenReturnsWithPublicVpn() {
        // when
        val result = creator.getVpnDetectionDetails(
            methods = mapOf("publicVPN" to true),
            confidence = null,
            originCountry = null
        )

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Public VPN"))
        TestCase.assertFalse(result.contains("Confidence:"))
        TestCase.assertFalse(result.contains("Origin Country:"))
    }

    @Test
    fun givenMethodsWithAllFields_whenGetVpnDetectionDetails_thenReturnsCompleteString() {
        // when
        val result = creator.getVpnDetectionDetails(
            methods = mapOf("timezoneMismatch" to true),
            confidence = "medium",
            originCountry = "DE"
        )

        // then
        TestCase.assertTrue(result.contains("Detected"))
        TestCase.assertTrue(result.contains("Timezone mismatch"))
        TestCase.assertTrue(result.contains("Confidence: medium"))
        TestCase.assertTrue(result.contains("Origin Country: Germany"))
    }

    // Tests for appendCountryInfo
    @Test
    fun givenNullOriginCountry_whenAppendCountryInfo_thenReturnsEmptyString() {
        // when
        val result = creator.appendCountryInfo(null)

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenValidCountryCode_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = creator.appendCountryInfo("US")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("United States"))
        TestCase.assertTrue(result.contains("\n"))
    }

    @Test
    fun givenLowerCaseCountryCode_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = creator.appendCountryInfo("us")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("United States"))
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = creator.appendCountryInfo("  DE  ")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("Germany"))
    }

    // Tests for appendConfidenceLevel
    @Test
    fun givenNullConfidence_whenAppendConfidenceLevel_thenReturnsEmptyString() {
        // when
        val result = creator.appendConfidenceLevel(null)

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenValidConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = creator.appendConfidenceLevel("high")

        // then
        TestCase.assertEquals("\nConfidence: high", result)
    }

    @Test
    fun givenMediumConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = creator.appendConfidenceLevel("medium")

        // then
        TestCase.assertEquals("\nConfidence: medium", result)
    }

    @Test
    fun givenLowConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = creator.appendConfidenceLevel("low")

        // then
        TestCase.assertEquals("\nConfidence: low", result)
    }

    // Tests for getFlagEmoji
    @Test
    fun givenValidCountryCode_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = creator.getFlagEmoji("US")

        // then
        TestCase.assertFalse(result.isEmpty())
        TestCase.assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result) // 🇺🇸
    }

    @Test
    fun givenLowerCaseCountryCode_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = creator.getFlagEmoji("us")

        // then
        TestCase.assertFalse(result.isEmpty())
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = creator.getFlagEmoji("  DE  ")

        // then
        TestCase.assertFalse(result.isEmpty())
    }

    @Test
    fun givenInvalidCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = creator.getFlagEmoji("USA")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenEmptyCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = creator.getFlagEmoji("")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenSingleCharacterCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = creator.getFlagEmoji("U")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenCountryCodeUS_whenGetFlagEmoji_thenReturnsUSFlag() {
        // when
        val result = creator.getFlagEmoji("US")

        // then
        TestCase.assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result) // 🇺🇸
    }

    @Test
    fun givenCountryCodeDE_whenGetFlagEmoji_thenReturnsGermanyFlag() {
        // when
        val result = creator.getFlagEmoji("DE")

        // then
        TestCase.assertEquals("\uD83C\uDDE9\uD83C\uDDEA", result) // 🇩🇪
    }

    @Test
    fun givenCountryCodeFR_whenGetFlagEmoji_thenReturnsFranceFlag() {
        // when
        val result = creator.getFlagEmoji("FR")

        // then
        TestCase.assertEquals("\uD83C\uDDEB\uD83C\uDDF7", result) // 🇫🇷
    }

    @Test
    fun givenCountryCodeGB_whenGetFlagEmoji_thenReturnsUnitedKingdomFlag() {
        // when
        val result = creator.getFlagEmoji("GB")

        // then
        TestCase.assertEquals("\uD83C\uDDEC\uD83C\uDDE7", result) // 🇬🇧
    }

    @Test
    fun givenCountryCodeJP_whenGetFlagEmoji_thenReturnsJapanFlag() {
        // when
        val result = creator.getFlagEmoji("JP")

        // then
        TestCase.assertEquals("\uD83C\uDDEF\uD83C\uDDF5", result) // 🇯🇵
    }

    @Test
    fun givenCountryCodeCA_whenGetFlagEmoji_thenReturnsCanadaFlag() {
        // when
        val result = creator.getFlagEmoji("CA")

        // then
        TestCase.assertEquals("\uD83C\uDDE8\uD83C\uDDE6", result) // 🇨🇦
    }

    @Test
    fun givenCountryCodeAU_whenGetFlagEmoji_thenReturnsAustraliaFlag() {
        // when
        val result = creator.getFlagEmoji("AU")

        // then
        TestCase.assertEquals("\uD83C\uDDE6\uD83C\uDDFA", result) // 🇦🇺
    }

    @Test
    fun givenCountryCodeBR_whenGetFlagEmoji_thenReturnsBrazilFlag() {
        // when
        val result = creator.getFlagEmoji("BR")

        // then
        TestCase.assertEquals("\uD83C\uDDE7\uD83C\uDDF7", result) // 🇧🇷
    }

    @Test
    fun givenCountryCodeCN_whenGetFlagEmoji_thenReturnsChinaFlag() {
        // when
        val result = creator.getFlagEmoji("CN")

        // then
        TestCase.assertEquals("\uD83C\uDDE8\uD83C\uDDF3", result) // 🇨🇳
    }

    @Test
    fun givenCountryCodeIN_whenGetFlagEmoji_thenReturnsIndiaFlag() {
        // when
        val result = creator.getFlagEmoji("IN")

        // then
        TestCase.assertEquals("\uD83C\uDDEE\uD83C\uDDF3", result) // 🇮🇳
    }

    @Test
    fun givenCountryCodeRU_whenGetFlagEmoji_thenReturnsRussiaFlag() {
        // when
        val result = creator.getFlagEmoji("RU")

        // then
        TestCase.assertEquals("\uD83C\uDDF7\uD83C\uDDFA", result) // 🇷🇺
    }

    @Test
    fun givenCountryCodeIT_whenGetFlagEmoji_thenReturnsItalyFlag() {
        // when
        val result = creator.getFlagEmoji("IT")

        // then
        TestCase.assertEquals("\uD83C\uDDEE\uD83C\uDDF9", result) // 🇮🇹
    }

    @Test
    fun givenCountryCodeES_whenGetFlagEmoji_thenReturnsSpainFlag() {
        // when
        val result = creator.getFlagEmoji("ES")

        // then
        TestCase.assertEquals("\uD83C\uDDEA\uD83C\uDDF8", result) // 🇪🇸
    }

    @Test
    fun givenCountryCodeMX_whenGetFlagEmoji_thenReturnsMexicoFlag() {
        // when
        val result = creator.getFlagEmoji("MX")

        // then
        TestCase.assertEquals("\uD83C\uDDF2\uD83C\uDDFD", result) // 🇲🇽
    }

    @Test
    fun givenCountryCodeKR_whenGetFlagEmoji_thenReturnsSouthKoreaFlag() {
        // when
        val result = creator.getFlagEmoji("KR")

        // then
        TestCase.assertEquals("\uD83C\uDDF0\uD83C\uDDF7", result) // 🇰🇷
    }

    // Tests for getCountryInfo
    @Test
    fun givenValidCountryCode_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = creator.getCountryInfo("US")

        // then
        TestCase.assertEquals("United States", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenValidCountryCodeDE_whenGetCountryInfo_thenReturnsGermany() {
        // when
        val result = creator.getCountryInfo("DE")

        // then
        TestCase.assertEquals("Germany", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenValidCountryCodeFR_whenGetCountryInfo_thenReturnsFrance() {
        // when
        val result = creator.getCountryInfo("FR")

        // then
        TestCase.assertEquals("France", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenLowerCaseCountryCode_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = creator.getCountryInfo("gb")

        // then
        TestCase.assertTrue(result.first.isNotEmpty())
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = creator.getCountryInfo("  JP  ")

        // then
        TestCase.assertTrue(result.first.isNotEmpty())
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenInvalidCountryCode_whenGetCountryInfo_thenReturnsCodeAsName() {
        // when
        val result = creator.getCountryInfo("XX")

        // then
        TestCase.assertEquals("XX", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }
}

private val expectedJson = """
{
    "identification": {
        "requestId": "1111111111111.AAAAAA",
        "visitorId": "rVC74CiaXVZGVC69OBsP",
        "confidenceScore": {
            "score": 1.0
        },
        "visitorFound": true,
        "ipAddress": "192.192.192.192",
        "ipLocation": {
            "accuracyRadius": 20,
            "latitude": 20.202,
            "longitude": 20.202,
            "postalCode": "123456",
            "timezone": "Europe / Berlin",
            "city": {
                "name": "Berlin"
            },
            "country": {
                "name": "Germany",
                "code": "DE"
            },
            "continent": {
                "name": "Europe",
                "code": "EU"
            },
            "subdivisions": [
                {
                    "name": "Berlin",
                    "isoCode": "DE-BE"
                }
            ]
        },
        "osName": "Android",
        "osVersion": "13",
        "firstSeenAt": {
            "global": "2024-01-16T01:01:01.587Z",
            "subscription": "2024-01-16T01:01:01.587Z"
        },
        "lastSeenAt": {
            "global": "2024-01-20T01:01:01.587Z",
            "subscription": "2024-01-20T01:01:01.587Z"
        }
    },
    "smartSignals": {
        "clonedApp": {
            "data": {
                "result": false
            }
        },
        "emulator": {
            "data": {
                "result": false
            }
        },
        "factoryReset": {
            "data": {
                "time": "2024-02-05T14:54:36Z",
                "timestamp": 1707144876
            }
        },
        "frida": {
            "data": {
                "result": false
            }
        },
        "highActivity": {
            "data": {
                "result": true,
                "dailyRequests": 125
            }
        },
        "locationSpoofing": {
            "data": {
                "result": false
            }
        },
        "rootApps": {
            "data": {
                "result": false
            }
        },
        "vpn": {
            "data": {
                "result": true,
                "originTimezone": "America/New_York",
                "originCountry": "DE",
                "confidence": "high",
                "methods": {
                    "timezoneMismatch": true,
                    "publicVPN": false,
                    "auxiliaryMobile": false,
                    "osMismatch": false
                }
            }
        }
    }
}
""".trimIndent()
