package com.portfolio.prototype_chat.networks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class ChildEventLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    constructor(ref: DatabaseReference) : this(query = ref)

    private val listener = IChildEventListener()

    override fun onActive() {
        super.onActive()
        query.addChildEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }


    private inner class IChildEventListener : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            value = snapshot
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            value = snapshot
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            value = snapshot
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            value = snapshot
        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
}