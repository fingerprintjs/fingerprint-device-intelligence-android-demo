package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.annotation.VisibleForTesting
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import java.util.Locale

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun SmartSignal.Vpn.getVpnStatusString(): String = when {
    !result -> NOT_DETECTED_STRING
    else -> getVpnDetectionDetails(methods, confidence, originCountry)
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun SmartSignal.Vpn.getVpnNoteString(): String = when {
    !result -> ""
    else -> VPN_NOTE_STRING
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun getVpnDetectionDetails(
    methods: Map<String, Boolean>,
    confidence: String?,
    originCountry: String?,
): String {
    val detectedMethod = VPN_METHOD_PRIORITY
        .firstOrNull { methods[it] == true }
        ?.let { " (${VPN_METHOD_LABELS[it]})" }
        .orEmpty()

    return "$DETECTED_STRING$detectedMethod${appendConfidenceLevel(confidence)}${appendCountryInfo(originCountry)}"
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun appendCountryInfo(originCountry: String?): String {
    if (originCountry.isNullOrBlank()) {
        return ""
    }
    val (countryName, flag) = getCountryInfo(originCountry)
    return "\nOrigin Country: $countryName $flag".trimEnd()
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun appendConfidenceLevel(confidence: String?): String {
    return confidence?.let { "\nConfidence: $it" }.orEmpty()
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun getFlagEmoji(countryCode: String): String {
    val normalized = countryCode.trim().uppercase(Locale.ROOT)
    if (normalized.length != COUNTRY_CODE_LENGTH) return ""

    val first = Character.codePointAt(normalized, 0) - ASCII_UPPERCASE_A_CODEPOINT + REGIONAL_INDICATOR_BASE
    val second = Character.codePointAt(normalized, 1) - ASCII_UPPERCASE_A_CODEPOINT + REGIONAL_INDICATOR_BASE

    return String(Character.toChars(first)) + String(Character.toChars(second))
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun getCountryInfo(code: String): Pair<String, String> {
    val normalized = code.trim().uppercase(Locale.ROOT)
    val name = Locale("", normalized).displayCountry
    val flag = getFlagEmoji(normalized)
    val displayName = if (name.isNotBlank()) name else normalized
    return displayName to flag
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun Boolean.detectionStatusString(): String {
    return if (this) DETECTED_STRING else NOT_DETECTED_STRING
}

const val NOT_DETECTED_STRING = "Not detected"
const val DETECTED_STRING = "Detected"
const val NOT_AVAILABLE_STRING = "N/A"
private const val VPN_NOTE_STRING = "Note: works without location permissions"

private val VPN_METHOD_LABELS = mapOf(
    "publicVPN" to "Public VPN",
    "timezoneMismatch" to "Timezone mismatch",
    "relay" to "Relay",
    "auxiliaryMobile" to "Auxiliary mobile",
)

private val VPN_METHOD_PRIORITY = listOf(
    "publicVPN",
    "timezoneMismatch",
    "relay",
    "auxiliaryMobile",
)

private const val COUNTRY_CODE_LENGTH = 2
private const val ASCII_UPPERCASE_A_CODEPOINT = 0x41
private const val REGIONAL_INDICATOR_BASE = 0x1F1E6
