package com.portfolio.prototype_chat.utils

import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


fun formatAMPM(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("a h:mm")
    return zonedDt.format(formatter)
}

fun formatDayOfWeek(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    return zonedDt.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.JAPAN)
}

fun formatMD(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("M/d")
    return zonedDt.format(formatter)
}

fun formatMDYY(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("M/d/YY")
    return zonedDt.format(formatter)
}

    
    
