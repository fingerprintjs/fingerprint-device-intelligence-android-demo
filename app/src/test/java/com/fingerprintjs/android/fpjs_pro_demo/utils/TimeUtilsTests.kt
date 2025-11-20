package com.fingerprintjs.android.fpjs_pro_demo.utils

import junit.framework.TestCase
import org.junit.Test

class TimeUtilsTests {

    companion object {
        private const val MILLIS_IN_SECOND = 1000L
        private const val SECONDS_IN_MINUTE = 60
        private const val MINUTES_IN_HOUR = 60
        private const val HOURS_IN_DAY = 24
        private const val DAYS_IN_WEEK = 7
    }

    @Test
    fun `returns Just now for seconds less than 60`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 30 * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (Just now)", result)
    }

    @Test
    fun `returns minutes ago for less than an hour`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 5 * SECONDS_IN_MINUTE * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (5 minutes ago)", result)
    }

    @Test
    fun `returns hours ago for less than a day`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 3 * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (3 hours ago)", result)
    }

    @Test
    fun `returns days ago for less than a week`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 2 * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (2 days ago)", result)
    }

    @Test
    fun `returns weeks ago for less than threshold`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 7 * DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (1 week ago)", result)
    }

    @Test
    fun `falls back to DateUtils for older timestamps`() {
        val now = 1_000_000_000_000L
        val timestamp = now - 40L * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND

        val result = relativeTime("12:00 PM", timestamp)
        assert(result.startsWith("12:00 PM ("))
    }

    @Test
    fun `getEpochTimestampFromTimeString parses valid ISO string`() {
        val timeString = "2024-01-01T12:00:00Z"

        val result = getEpochTimestampFromTimeString(timeString)

        TestCase.assertEquals(1704110400L, result)
    }

    @Test
    fun `getEpochTimestampFromTimeString returns null for invalid string`() {
        val result = getEpochTimestampFromTimeString("invalid-time")

        TestCase.assertNull(result)
    }

    @Test
    fun `getRelativeTimeString returns Not detected when timestamp invalid`() {
        val result = getRelativeTimeString("12:00 PM", 0L)

        TestCase.assertEquals("Not detected", result)
    }

    @Test
    fun `getRelativeTimeString delegates to relativeTime for valid inputs`() {
        val timestamp = System.currentTimeMillis() / MILLIS_IN_SECOND
        val time = "12:00 PM"

        val expected = relativeTime(time, timestamp)
        val result = getRelativeTimeString(time, timestamp)

        TestCase.assertEquals(expected, result)
    }
}
