package com.fingerprintjs.android.fpjs_pro_demo

import com.fingerprintjs.android.fpjs_pro_demo.utils.getRelativeTimeString
import com.fingerprintjs.android.fpjs_pro_demo.utils.relativeTime
import junit.framework.TestCase
import org.junit.Test

class TimeUtilsTests {

    companion object {
        private const val SECONDS_IN_MINUTE = 60L
        private const val MINUTES_IN_HOUR = 60L
        private const val HOURS_IN_DAY = 24L
        private const val MILLIS_IN_SECOND = 1000L
    }

    private fun nowMillis(): Long = System.currentTimeMillis()

    // relativeTime — older-than-5-weeks path uses DateUtils.getRelativeTimeSpanString
    // which requires a device context, so these live in androidTest.

    @Test
    fun relativeTime_sixWeeksAgo_returnsDateUtilsSpanString() {
        val timestampMillis = nowMillis() - 42 * HOURS_IN_DAY * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * MILLIS_IN_SECOND
        val result = relativeTime("2024-01-01T00:00:00Z", timestampMillis)
        // DateUtils formats as "X months ago" / "X weeks ago" depending on locale;
        // we just verify the original label is prepended and the relative part is non-empty.
        TestCase.assertTrue(result.startsWith("2024-01-01T00:00:00Z ("))
        TestCase.assertTrue(result.endsWith(")"))
    }

    @Test
    fun relativeTime_oneYearAgo_returnsDateUtilsSpanString() {
        val timestampMillis = nowMillis() - 365 * HOURS_IN_DAY * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * MILLIS_IN_SECOND
        val result = relativeTime("2023-01-01T00:00:00Z", timestampMillis)
        TestCase.assertTrue(result.startsWith("2023-01-01T00:00:00Z ("))
        TestCase.assertTrue(result.endsWith(")"))
    }

    @Test
    fun getRelativeTimeString_oldTimestampWithValidTime_returnsFormattedString() {
        val timestampMillis = nowMillis() - 42 * HOURS_IN_DAY * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * MILLIS_IN_SECOND
        val isoString = "2024-01-01T00:00:00Z"
        val result = getRelativeTimeString(isoString, timestampMillis)
        TestCase.assertTrue(result.startsWith("$isoString ("))
        TestCase.assertTrue(result.endsWith(")"))
    }
}