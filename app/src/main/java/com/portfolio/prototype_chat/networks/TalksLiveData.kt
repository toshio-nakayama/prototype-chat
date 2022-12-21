package com.portfolio.prototype_chat.networks

import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.models.db.Talk
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.NodeNames

class TalksLiveData(private val query: Query) : LiveData<Talk>() {
    constructor(ref: DatabaseReference) : this(query = ref)

    private var rootRef: DatabaseReference = Firebase.database.reference
    private var userRef: DatabaseReference = rootRef.child(NodeNames.USERS)
    private val listener: IChildEventListener = IChildEventListener()

    override fun onActive() {
        super.onActive()
        query.addChildEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }

    private fun createTalkBasedOnSnapshot(snapshot: DataSnapshot, userId: String) {
        val unreadCount = snapshot.child(NodeNames.UNREAD_COUNT).value?.toString()?.toInt() ?: 0
        val time = snapshot.child(NodeNames.TIME_STAMP).value.toString()
        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child(NodeNames.NAME).value.toString()
                val photoName = userId + Constants.EXT_JPG
                val talk = Talk(
                    userId = userId,
                    userName = userName,
                    photoName = photoName,
                    unreadCount = unreadCount,
                    time = time
                )
                value = talk
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    inner class IChildEventListener : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.key?.run { createTalkBasedOnSnapshot(snapshot, this) }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.key?.run { createTalkBasedOnSnapshot(snapshot, this) }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
}


