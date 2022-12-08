package com.portfolio.prototype_chat.home_ui.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.firebase_livedata.ChildEventLiveData

class MessageViewModel(private val talkUserId:String) : ViewModel(){

    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val messageRef:DatabaseReference = rootRef.child(NodeNames.MESSAGES).child(currentUser!!.uid).child(talkUserId)

    private val messageLiveData: ChildEventLiveData = ChildEventLiveData(messageRef)

    fun getMessageLiveData():LiveData<DataSnapshot> {
        return messageLiveData
    }
}