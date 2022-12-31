package com.portfolio.prototype_chat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData


fun connectionAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return capabilities?.let {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            false
        } else {
            false
        }
    } ?: false
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}


inline fun <T1, T2, R> guard(
    p1: T1?, p2: T2?,
    condition: Boolean = true,
    block: (T1, T2) -> R,
): R? = if (p1 != null && p2 != null && condition)
    block(p1, p2)
else null

inline fun <T1, T2, T3, R> guard(
    p1: T1?, p2: T2?, p3: T3?,
    condition: Boolean = true,
    block: (T1, T2, T3) -> R,
): R? = if (p1 != null && p2 != null && p3 != null && condition)
    block(p1, p2, p3)
else null

inline fun <T1, T2, T3, T4, R> guard(
    p1: T1?, p2: T2?, p3: T3?, p4: T4?,
    condition: Boolean = true,
    block: (T1, T2, T3, T4) -> R,
): R? = if (p1 != null && p2 != null && p3 != null && p4 != null && condition)
    block(p1, p2, p3, p4)
else null
