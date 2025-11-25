package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.annotation.VisibleForTesting
import com.fingerprintjs.android.fpjs_pro_demo.constants.StringConstants
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import java.util.Locale

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun SmartSignal.Vpn.getVpnStatusString(): String = when {
    !result -> StringConstants.NOT_DETECTED
    else -> getVpnDetectionDetails(methods, confidence, originCountry)
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun SmartSignal.Vpn.getVpnNoteString(): String = when {
    !result -> ""
    else -> StringConstants.VPN_NOTE_STRING
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun getVpnDetectionDetails(
    methods: Map<String, Boolean>,
    confidence: String?,
    originCountry: String?,
): String {
    val detectedMethod = StringConstants.VPN_METHOD_PRIORITY
        .firstOrNull { methods[it] == true }
        ?.let { " (${StringConstants.VPN_METHOD_LABELS[it]})" }
        .orEmpty()

    return "${StringConstants.DETECTED}$detectedMethod${appendConfidenceLevel(confidence)}${appendCountryInfo(originCountry)}"
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
    if (normalized.length != StringConstants.COUNTRY_CODE_LENGTH) return ""

    val first = Character.codePointAt(normalized, 0) - StringConstants.ASCII_UPPERCASE_A_CODEPOINT + StringConstants.REGIONAL_INDICATOR_BASE
    val second = Character.codePointAt(normalized, 1) - StringConstants.ASCII_UPPERCASE_A_CODEPOINT + StringConstants.REGIONAL_INDICATOR_BASE

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
    return if (this) StringConstants.DETECTED else StringConstants.NOT_DETECTED
}

