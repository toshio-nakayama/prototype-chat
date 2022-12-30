package com.portfolio.prototype_chat.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

private fun getZonedDateTime(millis: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
}

fun startTimeOfDay(millis: Long): Long {
    val zonedEpochMillis = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    return zonedEpochMillis.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
}

fun startDayOfWeek(zonedDateTime: ZonedDateTime, dayOfWeek: DayOfWeek): ZonedDateTime {
    return zonedDateTime.with(TemporalAdjusters.previousOrSame(dayOfWeek))
}

fun isSameYear(millis1: Long, millis2: Long): Boolean {
    val year1 = getZonedDateTime(millis1).year
    val year2 = getZonedDateTime(millis2).year
    return year1 == year2
}

fun isSameWeek(millis1: Long, millis2: Long): Boolean {
    val startDay1 = startDayOfWeek(getZonedDateTime(millis1), DayOfWeek.MONDAY)
    val startDay2 = startDayOfWeek(getZonedDateTime(millis2), DayOfWeek.MONDAY)
    return startDay1.compareTo(startDay2) == 0
}

fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val day1 = getZonedDateTime(startTimeOfDay(millis1))
    val day2 = getZonedDateTime(startTimeOfDay(millis2))
    return day1.compareTo(day2) == 0
}

fun isYesterday(millis: Long): Boolean {
    val day = getZonedDateTime(startTimeOfDay(millis))
    val yesterday = getZonedDateTime(startTimeOfDay(System.currentTimeMillis())).minusDays(1)
    return day.compareTo(yesterday) == 0
}