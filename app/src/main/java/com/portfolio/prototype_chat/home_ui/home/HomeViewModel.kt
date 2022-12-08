package com.portfolio.prototype_chat.home_ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.firebase_livedata.ValueEventLiveData
import com.portfolio.prototype_chat.signup.User

class HomeViewModel : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS).child(currentUser!!.uid)
    private val talkRef: DatabaseReference = rootRef.child(NodeNames.TALK).child(currentUser!!.uid)

    val userLiveData: LiveData<User> =
        Transformations.map(ValueEventLiveData(userRef)) { it.getValue(User::class.java) }

    val talkLiveData: ValueEventLiveData = ValueEventLiveData(talkRef)

}