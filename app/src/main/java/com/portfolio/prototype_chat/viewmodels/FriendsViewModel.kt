package com.portfolio.prototype_chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.networks.FriendsLiveData
import com.portfolio.prototype_chat.utils.NodeNames

class FriendsViewModel : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS)
    private val talkRef: DatabaseReference = rootRef.child(NodeNames.TALK)
    private val talkQuery: Query = talkRef.child(currentUser!!.uid).orderByChild(NodeNames.TIME_STAMP)
    val friendLiveData: LiveData<Friend> = FriendsLiveData(talkQuery)
    val friendListLiveData: MutableLiveData<MutableList<Friend>> by lazy { MutableLiveData<MutableList<Friend>>(mutableListOf()) }
}