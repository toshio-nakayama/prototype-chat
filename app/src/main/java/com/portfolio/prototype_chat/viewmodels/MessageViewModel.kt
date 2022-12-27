package com.portfolio.prototype_chat.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.utils.NodeNames

class MessageViewModel(guestId: String) : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val messageRef: DatabaseReference =
        rootRef.child(NodeNames.MESSAGES).child(currentUser!!.uid).child(guestId)
    
}