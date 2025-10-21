package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.text.format.DateUtils
const val TIME_STAMP = 10_000_000_000L
const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val HOURS_IN_DAY = 24
const val DAYS_IN_WEEK = 7
const val WEEK = 5
const val MILLIS_IN_SECOND = 1000L
fun relativeFactoryResetTime(time: String?, timestamp: Long): String {
    val timestampMillis = if (timestamp < TIME_STAMP) timestamp * MILLIS_IN_SECOND else timestamp
    val diff = System.currentTimeMillis() - timestampMillis

    val seconds = diff / MILLIS_IN_SECOND
    val minutes = seconds / SECONDS_IN_MINUTE
    val hours = minutes / MINUTES_IN_HOUR
    val days = hours / HOURS_IN_DAY
    val weeks = days / DAYS_IN_WEEK

    val relative = when {
        seconds < SECONDS_IN_MINUTE -> "Just now"
        minutes < MINUTES_IN_HOUR -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        hours < HOURS_IN_DAY -> "$hours hour${if (hours > 1) "s" else ""} ago"
        days < DAYS_IN_WEEK -> "$days day${if (days > 1) "s" else ""} ago"
        weeks < WEEK -> "$weeks week${if (weeks > 1) "s" else ""} ago"
        else -> DateUtils.getRelativeTimeSpanString(
            timestamp * MILLIS_IN_SECOND,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )
    }
    return "$time ($relative)"
}
