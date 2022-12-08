package com.portfolio.prototype_chat.firebase_livedata

import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class SingleValueEventLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    constructor(ref: DatabaseReference) : this(query = ref)

    private val listener = IValueEventListener()

    override fun onActive() {
        super.onActive()
        query.addListenerForSingleValueEvent(listener)
    }

    private inner class IValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            value = snapshot
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

}