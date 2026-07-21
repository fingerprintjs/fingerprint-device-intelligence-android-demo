package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro_demo.constants.StringConstants
import junit.framework.TestCase
import org.junit.Test

class TimeUtilsUnitTests {

    // region epochMillisToIsoString

    @Test
    fun epochMillisToIsoString_zeroMillis_returnsUnixEpochString() {
        TestCase.assertEquals("1970-01-01T00:00:00Z", epochMillisToIsoString(0L))
    }

    @Test
    fun epochMillisToIsoString_knownMillis_returnsCorrectIsoString() {
        // 2024-01-01T00:00:00Z = 1704067200000 ms
        TestCase.assertEquals("2024-01-01T00:00:00Z", epochMillisToIsoString(1704067200000L))
    }

    @Test
    fun epochMillisToIsoString_negativeMillis_returnsPreEpochString() {
        // -1000 ms = 1969-12-31T23:59:59Z
        TestCase.assertEquals("1969-12-31T23:59:59Z", epochMillisToIsoString(-1000L))
    }

    // endregion

    // region epochSecondsToIsoString

    @Test
    fun epochSecondsToIsoString_zeroSeconds_returnsUnixEpochString() {
        TestCase.assertEquals("1970-01-01T00:00:00Z", epochSecondsToIsoString(0L))
    }

    @Test
    fun epochSecondsToIsoString_knownSeconds_returnsCorrectIsoString() {
        // 2024-01-01T00:00:00Z = 1704067200 s
        TestCase.assertEquals("2024-01-01T00:00:00Z", epochSecondsToIsoString(1704067200L))
    }

    @Test
    fun epochSecondsToIsoString_negativeSeconds_returnsPreEpochString() {
        // -1 s = 1969-12-31T23:59:59Z
        TestCase.assertEquals("1969-12-31T23:59:59Z", epochSecondsToIsoString(-1L))
    }

    @Test
    fun epochSecondsToIsoString_overflowValue_returnsNull() {
        // Long.MAX_VALUE s is far beyond Instant.MAX, expect exception caught
        TestCase.assertNull(epochSecondsToIsoString(Long.MAX_VALUE))
    }

    // endregion

    // region getRelativeTimeString — guard conditions (no DateUtils path reached)

    @Test
    fun getRelativeTimeString_nullTime_returnsNotDetected() {
        TestCase.assertEquals(StringConstants.NOT_DETECTED, getRelativeTimeString(null, 1704067200000L))
    }

    @Test
    fun getRelativeTimeString_blankTime_returnsNotDetected() {
        TestCase.assertEquals(StringConstants.NOT_DETECTED, getRelativeTimeString("   ", 1704067200000L))
    }

    @Test
    fun getRelativeTimeString_emptyTime_returnsNotDetected() {
        TestCase.assertEquals(StringConstants.NOT_DETECTED, getRelativeTimeString("", 1704067200000L))
    }

    @Test
    fun getRelativeTimeString_zeroTimestamp_returnsNotDetected() {
        TestCase.assertEquals(StringConstants.NOT_DETECTED, getRelativeTimeString("2024-01-01T00:00:00Z", 0L))
    }

    @Test
    fun getRelativeTimeString_negativeTimestamp_returnsNotDetected() {
        TestCase.assertEquals(StringConstants.NOT_DETECTED, getRelativeTimeString("2024-01-01T00:00:00Z", -1L))
    }

    // endregion

    // region relativeTime — recent timestamp paths (no DateUtils)

    @Test
    fun relativeTime_justNow_returnsJustNow() {
        val timestampMillis = System.currentTimeMillis() - 30_000L // 30 seconds ago
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains(StringConstants.JUST_NOW))
    }

    @Test
    fun relativeTime_fiveMinutesAgo_containsMinutesAgo() {
        val timestampMillis = System.currentTimeMillis() - 5 * 60_000L
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains("5"))
        TestCase.assertTrue(result.contains(StringConstants.MINUTES))
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    @Test
    fun relativeTime_oneMinuteAgo_usessingularMinute() {
        val timestampMillis = System.currentTimeMillis() - 90_000L // 1.5 min → 1 minute
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains(StringConstants.MINUTE))
        TestCase.assertFalse(result.contains(StringConstants.MINUTES))
    }

    @Test
    fun relativeTime_threeHoursAgo_containsHoursAgo() {
        val timestampMillis = System.currentTimeMillis() - 3 * 3600_000L
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains("3"))
        TestCase.assertTrue(result.contains(StringConstants.HOURS))
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    @Test
    fun relativeTime_oneHourAgo_usesSingularHour() {
        val timestampMillis = System.currentTimeMillis() - 90 * 60_000L // 1.5 h → 1 hour
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains(StringConstants.HOUR))
        TestCase.assertFalse(result.contains(StringConstants.HOURS))
    }

    @Test
    fun relativeTime_threeDaysAgo_containsDaysAgo() {
        val timestampMillis = System.currentTimeMillis() - 3 * 86400_000L
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains("3"))
        TestCase.assertTrue(result.contains(StringConstants.DAYS))
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    @Test
    fun relativeTime_oneDayAgo_usesSingularDay() {
        val timestampMillis = System.currentTimeMillis() - 36 * 3600_000L // 1.5 days → 1 day
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains(StringConstants.DAY))
        TestCase.assertFalse(result.contains(StringConstants.DAYS))
    }

    @Test
    fun relativeTime_twoWeeksAgo_containsWeeksAgo() {
        val timestampMillis = System.currentTimeMillis() - 14 * 86400_000L
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains("2"))
        TestCase.assertTrue(result.contains(StringConstants.WEEKS))
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    @Test
    fun relativeTime_includesOriginalTimeString() {
        val label = "2024-01-01T00:00:00Z"
        val timestampMillis = System.currentTimeMillis() - 5 * 60_000L
        val result = relativeTime(label, timestampMillis)
        TestCase.assertTrue(result.startsWith(label))
    }

    @Test
    fun relativeTime_epochMillisTimestamp_handledCorrectly() {
        // Timestamps > TIME_STAMP (10B) are treated as millis directly
        val timestampMillis = System.currentTimeMillis() - 5 * 60_000L
        TestCase.assertTrue(timestampMillis > TIME_STAMP)
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    @Test
    fun relativeTime_epochSecondsTimestamp_multipliedToMillis() {
        // Timestamps < TIME_STAMP (10B) are treated as seconds and multiplied by 1000
        val timestampSeconds = System.currentTimeMillis() / 1000 - 5 * 60
        TestCase.assertTrue(timestampSeconds < TIME_STAMP)
        val result = relativeTime("2024-01-01T00:00:00Z", timestampSeconds)
        TestCase.assertTrue(result.contains(StringConstants.AGO))
    }

    // endregion
}
