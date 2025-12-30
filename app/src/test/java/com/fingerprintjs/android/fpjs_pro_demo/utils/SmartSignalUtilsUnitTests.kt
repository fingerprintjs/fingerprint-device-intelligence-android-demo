package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import junit.framework.TestCase
import org.junit.Test

class SmartSignalUtilsUnitTests {
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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnStatusString()

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
        val result = vpn.getVpnNoteString()

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
        val result = vpn.getVpnNoteString()

        // then
        TestCase.assertEquals("Note: works without location permissions", result)
    }

    // Tests for getVpnDetectionDetails
    @Test
    fun givenEmptyMethods_whenGetVpnDetectionDetails_thenReturnsDetectedWithoutMethod() {
        // when
        val result = getVpnDetectionDetails(
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
        val result = getVpnDetectionDetails(
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
        val result = getVpnDetectionDetails(
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
        val result = appendCountryInfo(null)

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenValidCountryCode_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = appendCountryInfo("US")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("United States"))
        TestCase.assertTrue(result.contains("\n"))
    }

    @Test
    fun givenLowerCaseCountryCode_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = appendCountryInfo("us")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("United States"))
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenAppendCountryInfo_thenReturnsFormattedString() {
        // when
        val result = appendCountryInfo("  DE  ")

        // then
        TestCase.assertTrue(result.contains("Origin Country:"))
        TestCase.assertTrue(result.contains("Germany"))
    }

    // Tests for appendConfidenceLevel
    @Test
    fun givenNullConfidence_whenAppendConfidenceLevel_thenReturnsEmptyString() {
        // when
        val result = appendConfidenceLevel(null)

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenValidConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = appendConfidenceLevel("high")

        // then
        TestCase.assertEquals("\nConfidence: high", result)
    }

    @Test
    fun givenMediumConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = appendConfidenceLevel("medium")

        // then
        TestCase.assertEquals("\nConfidence: medium", result)
    }

    @Test
    fun givenLowConfidence_whenAppendConfidenceLevel_thenReturnsFormattedString() {
        // when
        val result = appendConfidenceLevel("low")

        // then
        TestCase.assertEquals("\nConfidence: low", result)
    }

    // Tests for getFlagEmoji
    @Test
    fun givenValidCountryCode_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = getFlagEmoji("US")

        // then
        TestCase.assertFalse(result.isEmpty())
        TestCase.assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result) // 🇺🇸
    }

    @Test
    fun givenLowerCaseCountryCode_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = getFlagEmoji("us")

        // then
        TestCase.assertFalse(result.isEmpty())
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenGetFlagEmoji_thenReturnsFlagEmoji() {
        // when
        val result = getFlagEmoji("  DE  ")

        // then
        TestCase.assertFalse(result.isEmpty())
    }

    @Test
    fun givenInvalidCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = getFlagEmoji("USA")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenEmptyCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = getFlagEmoji("")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenSingleCharacterCountryCode_whenGetFlagEmoji_thenReturnsEmptyString() {
        // when
        val result = getFlagEmoji("U")

        // then
        TestCase.assertEquals("", result)
    }

    @Test
    fun givenCountryCodeUS_whenGetFlagEmoji_thenReturnsUSFlag() {
        // when
        val result = getFlagEmoji("US")

        // then
        TestCase.assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result) // 🇺🇸
    }

    @Test
    fun givenCountryCodeDE_whenGetFlagEmoji_thenReturnsGermanyFlag() {
        // when
        val result = getFlagEmoji("DE")

        // then
        TestCase.assertEquals("\uD83C\uDDE9\uD83C\uDDEA", result) // 🇩🇪
    }

    @Test
    fun givenCountryCodeFR_whenGetFlagEmoji_thenReturnsFranceFlag() {
        // when
        val result = getFlagEmoji("FR")

        // then
        TestCase.assertEquals("\uD83C\uDDEB\uD83C\uDDF7", result) // 🇫🇷
    }

    @Test
    fun givenCountryCodeGB_whenGetFlagEmoji_thenReturnsUnitedKingdomFlag() {
        // when
        val result = getFlagEmoji("GB")

        // then
        TestCase.assertEquals("\uD83C\uDDEC\uD83C\uDDE7", result) // 🇬🇧
    }

    @Test
    fun givenCountryCodeJP_whenGetFlagEmoji_thenReturnsJapanFlag() {
        // when
        val result = getFlagEmoji("JP")

        // then
        TestCase.assertEquals("\uD83C\uDDEF\uD83C\uDDF5", result) // 🇯🇵
    }

    @Test
    fun givenCountryCodeCA_whenGetFlagEmoji_thenReturnsCanadaFlag() {
        // when
        val result = getFlagEmoji("CA")

        // then
        TestCase.assertEquals("\uD83C\uDDE8\uD83C\uDDE6", result) // 🇨🇦
    }

    @Test
    fun givenCountryCodeAU_whenGetFlagEmoji_thenReturnsAustraliaFlag() {
        // when
        val result = getFlagEmoji("AU")

        // then
        TestCase.assertEquals("\uD83C\uDDE6\uD83C\uDDFA", result) // 🇦🇺
    }

    @Test
    fun givenCountryCodeBR_whenGetFlagEmoji_thenReturnsBrazilFlag() {
        // when
        val result = getFlagEmoji("BR")

        // then
        TestCase.assertEquals("\uD83C\uDDE7\uD83C\uDDF7", result) // 🇧🇷
    }

    @Test
    fun givenCountryCodeCN_whenGetFlagEmoji_thenReturnsChinaFlag() {
        // when
        val result = getFlagEmoji("CN")

        // then
        TestCase.assertEquals("\uD83C\uDDE8\uD83C\uDDF3", result) // 🇨🇳
    }

    @Test
    fun givenCountryCodeIN_whenGetFlagEmoji_thenReturnsIndiaFlag() {
        // when
        val result = getFlagEmoji("IN")

        // then
        TestCase.assertEquals("\uD83C\uDDEE\uD83C\uDDF3", result) // 🇮🇳
    }

    @Test
    fun givenCountryCodeRU_whenGetFlagEmoji_thenReturnsRussiaFlag() {
        // when
        val result = getFlagEmoji("RU")

        // then
        TestCase.assertEquals("\uD83C\uDDF7\uD83C\uDDFA", result) // 🇷🇺
    }

    @Test
    fun givenCountryCodeIT_whenGetFlagEmoji_thenReturnsItalyFlag() {
        // when
        val result = getFlagEmoji("IT")

        // then
        TestCase.assertEquals("\uD83C\uDDEE\uD83C\uDDF9", result) // 🇮🇹
    }

    @Test
    fun givenCountryCodeES_whenGetFlagEmoji_thenReturnsSpainFlag() {
        // when
        val result = getFlagEmoji("ES")

        // then
        TestCase.assertEquals("\uD83C\uDDEA\uD83C\uDDF8", result) // 🇪🇸
    }

    @Test
    fun givenCountryCodeMX_whenGetFlagEmoji_thenReturnsMexicoFlag() {
        // when
        val result = getFlagEmoji("MX")

        // then
        TestCase.assertEquals("\uD83C\uDDF2\uD83C\uDDFD", result) // 🇲🇽
    }

    @Test
    fun givenCountryCodeKR_whenGetFlagEmoji_thenReturnsSouthKoreaFlag() {
        // when
        val result = getFlagEmoji("KR")

        // then
        TestCase.assertEquals("\uD83C\uDDF0\uD83C\uDDF7", result) // 🇰🇷
    }

    // Tests for getCountryInfo
    @Test
    fun givenValidCountryCode_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = getCountryInfo("US")

        // then
        TestCase.assertEquals("United States", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenValidCountryCodeDE_whenGetCountryInfo_thenReturnsGermany() {
        // when
        val result = getCountryInfo("DE")

        // then
        TestCase.assertEquals("Germany", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenValidCountryCodeFR_whenGetCountryInfo_thenReturnsFrance() {
        // when
        val result = getCountryInfo("FR")

        // then
        TestCase.assertEquals("France", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenLowerCaseCountryCode_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = getCountryInfo("gb")

        // then
        TestCase.assertTrue(result.first.isNotEmpty())
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenCountryCodeWithWhitespace_whenGetCountryInfo_thenReturnsNameAndFlag() {
        // when
        val result = getCountryInfo("  JP  ")

        // then
        TestCase.assertTrue(result.first.isNotEmpty())
        TestCase.assertFalse(result.second.isEmpty())
    }

    @Test
    fun givenInvalidCountryCode_whenGetCountryInfo_thenReturnsCodeAsName() {
        // when
        val result = getCountryInfo("XX")

        // then
        TestCase.assertEquals("XX", result.first)
        TestCase.assertFalse(result.second.isEmpty())
    }

    // Tests for getProximityDetails
    @Test
    fun givenProximityWithAllFields_whenGetProximityDetails_thenReturnsCompleteString() {
        // given
        val proximity = SmartSignal.Proximity(
            id = "testId123",
            precisionRadius = 10,
            confidence = 0.76f
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertTrue(result.contains("Proximity ID: testId123"))
        TestCase.assertTrue(result.contains("Precision Radius: 10m"))
        TestCase.assertTrue(result.contains("Confidence: 0.76"))
    }

    @Test
    fun givenProximityWithOnlyId_whenGetProximityDetails_thenReturnsIdOnly() {
        // given
        val proximity = SmartSignal.Proximity(
            id = "testId123",
            precisionRadius = null,
            confidence = null
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertEquals("Proximity ID: testId123", result)
    }

    @Test
    fun givenProximityWithIdAndPrecisionRadius_whenGetProximityDetails_thenReturnsIdAndPrecisionRadius() {
        // given
        val proximity = SmartSignal.Proximity(
            id = "testId123",
            precisionRadius = 15,
            confidence = null
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertTrue(result.contains("Proximity ID: testId123"))
        TestCase.assertTrue(result.contains("Precision Radius: 15m"))
        TestCase.assertFalse(result.contains("Confidence:"))
    }

    @Test
    fun givenProximityWithAllNullFields_whenGetProximityDetails_thenReturnsRequiresPermission() {
        // given
        val proximity = SmartSignal.Proximity(
            id = null,
            precisionRadius = null,
            confidence = null
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertEquals("Requires location permission", result)
    }

    @Test
    fun givenProximityWithOnlyConfidence_whenGetProximityDetails_thenReturnsConfidence() {
        // given
        val proximity = SmartSignal.Proximity(
            id = null,
            precisionRadius = null,
            confidence = 0.5f
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertTrue(result.contains("Confidence: 0.5"))
        TestCase.assertFalse(result.contains("Proximity ID:"))
        TestCase.assertFalse(result.contains("Precision Radius:"))
    }

    @Test
    fun givenProximityWithIdAndConfidence_whenGetProximityDetails_thenReturnsIdAndConfidence() {
        // given
        val proximity = SmartSignal.Proximity(
            id = "testId123",
            precisionRadius = null,
            confidence = 0.85f
        )

        // when
        val result = proximity.getProximityDetails()

        // then
        TestCase.assertTrue(result.contains("Proximity ID: testId123"))
        TestCase.assertTrue(result.contains("Confidence: 0.85"))
        TestCase.assertFalse(result.contains("Precision Radius:"))
    }
}
