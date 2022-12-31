package com.portfolio.prototype_chat.networks

import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class ValueEventLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    constructor(ref: DatabaseReference) : this(query = ref)
    
    private val listener = IValueEventListener()
    
    override fun onActive() {
        super.onActive()
        query.addValueEventListener(listener)
    }
    
    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }
    
    private inner class IValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            value = snapshot
        }
        
        override fun onCancelled(error: DatabaseError) {
        
        }
        
    }
    
}