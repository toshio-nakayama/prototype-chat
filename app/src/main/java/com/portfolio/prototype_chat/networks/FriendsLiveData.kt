package com.portfolio.prototype_chat.networks

import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.NodeNames

class FriendsLiveData(private val query: Query) : LiveData<Friend>() {
    constructor(ref: DatabaseReference) : this(query = ref)
    
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS)
    private val listener: IChildEventListener = IChildEventListener()
    
    override fun onActive() {
        super.onActive()
        query.addChildEventListener(listener)
    }
    
    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }
    
    private fun createFriendBasedOnSnapshot(snapshot: DataSnapshot, userId: String) {
        
        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    val name = user.name
                    val statusMessage = user.statusMessage
                    val photoName = user.photo
                    val friend = Friend(userId, name, statusMessage, photoName)
                    value = friend
                    
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
            
            }
            
        })
    }
    
    inner class IChildEventListener : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.key?.let { createFriendBasedOnSnapshot(snapshot, it) }
        }
        
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.key?.let { createFriendBasedOnSnapshot(snapshot, it) }
        }
        
        override fun onChildRemoved(snapshot: DataSnapshot) {
        
        }
        
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        
        }
        
        override fun onCancelled(error: DatabaseError) {
        
        }
        
    }
    
    
}