package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.text.format.DateUtils
import com.fingerprintjs.android.fpjs_pro_demo.constants.StringConstants
import java.time.Instant
import java.time.format.DateTimeParseException

const val TIME_STAMP = 10_000_000_000L
const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val HOURS_IN_DAY = 24
const val DAYS_IN_WEEK = 7
const val WEEK = 5
const val MILLIS_IN_SECOND = 1000L

fun relativeTime(time: String?, timestamp: Long): String {
    val timestampMillis = if (timestamp < TIME_STAMP) timestamp * MILLIS_IN_SECOND else timestamp
    val diff = System.currentTimeMillis() - timestampMillis

    val seconds = diff / MILLIS_IN_SECOND
    val minutes = seconds / SECONDS_IN_MINUTE
    val hours = minutes / MINUTES_IN_HOUR
    val days = hours / HOURS_IN_DAY
    val weeks = days / DAYS_IN_WEEK

    val relative = when {
        seconds < SECONDS_IN_MINUTE -> StringConstants.JUST_NOW
        minutes < MINUTES_IN_HOUR -> "$minutes ${if (minutes > 1) StringConstants.MINUTES else StringConstants.MINUTE} ${StringConstants.AGO}"
        hours < HOURS_IN_DAY -> "$hours ${if (hours > 1) StringConstants.HOURS else StringConstants.HOUR} ${StringConstants.AGO}"
        days < DAYS_IN_WEEK -> "$days ${if (days > 1) StringConstants.DAYS else StringConstants.DAY} ${StringConstants.AGO}"
        weeks < WEEK -> "$weeks ${if (weeks > 1) StringConstants.WEEKS else StringConstants.WEEK} ${StringConstants.AGO}"
        else -> DateUtils.getRelativeTimeSpanString(
            timestamp * MILLIS_IN_SECOND,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )
    }
    return "$time (${relative})"
}

fun getEpochTimestampFromTimeString(time: String): Long? {
    return try {
        Instant.parse(time).epochSecond
    } catch (e: DateTimeParseException) {
        null
    }
}

fun getRelativeTimeString(time: String?, timestamp: Long): String {
    return if (timestamp <= 0 || time?.isBlank() == true) {
        StringConstants.NOT_DETECTED
    } else {
        relativeTime(time, timestamp)
    }
}
