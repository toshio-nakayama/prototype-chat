package com.portfolio.prototype_chat.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.SimpleDateFormat
import java.util.*


fun connectionAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            result = true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            result = false
        }
    }
    return result
}

fun timestampToString(time:Long, pattern:String):String{
    val sdf = SimpleDateFormat(pattern, Locale.JAPAN)
    val dateTime = sdf.format(Date(time))
    return dateTime.split(" ")[1]
}