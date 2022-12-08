package com.portfolio.prototype_chat.home_ui.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.firebase_livedata.ChildEventLiveData

class TalkViewModel : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val talkRef: DatabaseReference = rootRef.child(NodeNames.TALK)
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS)

    val talkQuery: Query = talkRef.child(currentUser!!.uid).orderByChild(NodeNames.TIME_STAMP)
    val talkQueryLiveData: LiveData<DataSnapshot> = ChildEventLiveData(talkQuery)
    val talkLists : MutableLiveData<MutableList<Talk>> by lazy { MutableLiveData<MutableList<Talk>>(
        mutableListOf()) }
}