package com.portfolio.prototype_chat.util

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
import com.portfolio.prototype_chat.common.NodeNames
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

fun timestampToString(time: Long, pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.JAPAN)
    val dateTime = sdf.format(Date(time))
    return dateTime.split(" ")[1]
}

fun updateTalkDetails(context: Context, userId: String, friendId: String) {
    val dbRootRef = Firebase.database.reference
    dbRootRef.child(NodeNames.TALK).child(friendId).child(userId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentCount = "0"
                snapshot.child(NodeNames.UNREAD_COUNT).value?.let { currentCount = it.toString() }
                val talkUserRoot = "${NodeNames.TALK}/$friendId/$userId/"
                val talkUpdates = hashMapOf<String, Any>(
                    NodeNames.TIME_STAMP to ServerValue.TIMESTAMP,
                    NodeNames.UNREAD_COUNT to currentCount.toInt() + 1
                )
                val childUpdates = hashMapOf<String, Any>(
                    talkUserRoot to talkUpdates
                )
                dbRootRef.updateChildren(childUpdates)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun glideSupport(context: Context, uri: Uri, @DrawableRes placeholder: Int, into: ImageView) {
    Glide.with(context)
        .load(uri)
        .placeholder(placeholder)
        .error(placeholder)
        .into(into)
}
