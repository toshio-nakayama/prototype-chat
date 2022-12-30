package com.portfolio.prototype_chat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

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

fun timestampToString(time: Long, pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.JAPAN)
    val dateTime = sdf.format(Date(time))
    return dateTime.split(" ")[1]
}

fun updateTalkDetails(hostId: String, guestId: String, lastMessage: String) {
    val rootRef = Firebase.database.reference
    rootRef.child(NodeNames.TALK).child(guestId).child(hostId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentCount = "0"
                snapshot.child(NodeNames.UNREAD_COUNT).value?.let { currentCount = it.toString() }
                val guestRef = "${NodeNames.TALK}/$guestId/$hostId/"
                val talkUpdates = hashMapOf(
                    NodeNames.TIME_STAMP to ServerValue.TIMESTAMP,
                    NodeNames.UNREAD_COUNT to currentCount.toInt() + 1,
                    NodeNames.LAST_MESSAGE to lastMessage,
                    NodeNames.LAST_MESSAGE_TIME to ServerValue.TIMESTAMP
                )
                val childUpdates = hashMapOf<String, Any>(
                    guestRef to talkUpdates
                )
                rootRef.updateChildren(childUpdates)
            }
            
            override fun onCancelled(error: DatabaseError) {
            
            }
            
        })
}

fun getTimeAgo(time: Long): String {
    val secondMillis = 1000
    val minuteMillis = 60 * secondMillis
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    
    time * 1000
    
    val now: Long = System.currentTimeMillis();
    if (now < time || time <= 0) {
        return ""
    }
    val diff: Long = now - time
    return if (diff < minuteMillis) {
        "just now"
    } else if (diff < 2 * minuteMillis) {
        "a minute ago"
    } else if (diff < 59 * minuteMillis) {
        "${diff / minuteMillis} minutes ago"
    } else if (diff < 90 * minuteMillis) {
        "an hour ago"
    } else if (diff < 24 * hourMillis) {
        "${diff / hourMillis}hours ago"
    } else if (diff < 48 * hourMillis) {
        "yesterday"
    } else {
        "${diff / dayMillis}days ago"
    }
}

fun formatMessageTime(epochMillis: Long): String {
    //現在の日時を取得
    val currentMillis = System.currentTimeMillis()
    
    return if (isSameDay(epochMillis, currentMillis)) {//当日判定
        //epochMillisをformat　午前〇:〇〇　　午後〇:〇〇
        ""
    } else if (isYesterday(epochMillis)) {//昨日判定
        //昨日
        ""
    } else if (isSameWeek(epochMillis, currentMillis)) {//同じ週か判定
        //epochMillisの曜日
        ""
    } else if (isSameYear(epochMillis, currentMillis)) {//同じ年か判定
        //　月/日
        ""
    } else {
        //　月/日/西暦の下2桁
        ""
    }
    
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun glideSupport(context: Context, uri: Uri?, @DrawableRes placeholder: Int, into: ImageView) {
    Glide.with(context)
        .load(uri)
        .placeholder(placeholder)
        .error(placeholder)
        .into(into)
}

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
