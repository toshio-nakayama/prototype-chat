package com.portfolio.prototype_chat.utils

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R

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

fun formatMessageTime(context: Context, epochMillis: Long): String {
    val currentMillis = System.currentTimeMillis()
    return if (isSameDay(epochMillis, currentMillis)) {
        formatAMPM(epochMillis)
    } else if (isYesterday(epochMillis)) {
        context.getString(R.string.yesterday)
    } else if (isSameWeek(epochMillis, currentMillis)) {
        formatDayOfWeek(epochMillis)
    } else if (isSameYear(epochMillis, currentMillis)) {
        formatMD(epochMillis)
    } else {
        formatMDYY(epochMillis)
    }
    
}