package com.fingerprintjs.android.fpjs_pro_demo

import com.fingerprintjs.android.fpjs_pro_demo.utils.relativeFactoryResetTime
import junit.framework.TestCase
import org.junit.Test

class TimeUtilsTests {

    companion object {
        private const val SECONDS_IN_MINUTE = 60L
        private const val MINUTES_IN_HOUR = 60L
        private const val HOURS_IN_DAY = 24L
        private const val DAYS_IN_WEEK = 7L
    }

    private fun nowSeconds(): Long =
        System.currentTimeMillis() / 1000

    @Test
    fun ReturnsJustnowForSecondsLessThan60() {
        val now = nowSeconds()
        val timestamp = now - 30

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (Just now)", result)
    }

    @Test
    fun ReturnsMinutesAgoForLessThanAnHour() {
        val now = nowSeconds()
        val timestamp = now - 5 * SECONDS_IN_MINUTE

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (5 minutes ago)", result)
    }

    @Test
    fun ReturnsHoursAgoForLessThanADay() {
        val now = nowSeconds()
        val timestamp = now - 3 * MINUTES_IN_HOUR * SECONDS_IN_MINUTE

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (3 hours ago)", result)
    }

    @Test
    fun ReturnsDaysAgoForLessThanAWeek() {
        val now = nowSeconds()
        val timestamp = now - 2 * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (2 days ago)", result)
    }

    @Test
    fun ReturnsWeeksAgoForLessThanThreshold() {
        val now = nowSeconds()
        val timestamp =
            now - DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertEquals("12:00 PM (1 week ago)", result)
    }

    @Test
    fun FallsBackToDateUtilsForOlderTimestamps() {
        val now = nowSeconds()
        val timestamp =
            now - 40L * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE

        val result = relativeFactoryResetTime("12:00 PM", timestamp)
        TestCase.assertTrue(result.startsWith("12:00 PM ("))
    }
}
