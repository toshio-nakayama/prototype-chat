package com.portfolio.prototype_chat.home_ui.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.NodeNames

class TalksViewModel : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val talkRef: DatabaseReference = rootRef.child(NodeNames.TALK)

    private val talkQuery: Query = talkRef.child(currentUser!!.uid).orderByChild(NodeNames.TIME_STAMP)
    val talkLiveData: LiveData<Talk> = TalksLiveData(talkQuery)
    val talkList : MutableLiveData<MutableList<Talk>> by lazy { MutableLiveData<MutableList<Talk>>(
        mutableListOf()) }
}