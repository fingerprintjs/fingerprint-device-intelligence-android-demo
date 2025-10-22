package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.compose.ui.text.AnnotatedString
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
        val now = System.currentTimeMillis()
        val timestamp = (now - 30 * MILLIS_IN_SECOND)

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (Just now)", result)
    }

    @Test
    fun `returns minutes ago for less than an hour`() {
        val now = System.currentTimeMillis()
        val timestamp = (now - 5 * SECONDS_IN_MINUTE * MILLIS_IN_SECOND)

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (5 minutes ago)", result)
    }

    @Test
    fun `returns hours ago for less than a day`() {
        val now = System.currentTimeMillis()
        val timestamp = (now - 3 * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND)

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (3 hours ago)", result)
    }

    @Test
    fun `returns days ago for less than a week`() {
        val now = System.currentTimeMillis()
        val timestamp = (now - 2 * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND)

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (2 days ago)", result)
    }

    @Test
    fun `returns weeks ago for less than threshold`() {
        val now = System.currentTimeMillis()
        val timestamp = (now - 1 * DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND)

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (1 week ago)", result)
    }
}
