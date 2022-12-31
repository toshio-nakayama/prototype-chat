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

fun formatMMDD(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("MM/dd")
    return zonedDt.format(formatter)
}

fun formatMMDDYY(epochMillis: Long): String {
    val zonedDt = getZonedDateTime(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("MM/dd/YY")
    return zonedDt.format(formatter)
}

    
    
