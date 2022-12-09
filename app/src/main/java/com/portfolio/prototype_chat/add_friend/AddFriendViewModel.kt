package com.portfolio.prototype_chat.add_friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.firebase_livedata.SingleValueEventLiveData
import com.portfolio.prototype_chat.signup.User

class AddFriendViewModel(val userId: String) : ViewModel() {
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS).child(userId)
    val user: LiveData<User> =
        Transformations.map(SingleValueEventLiveData(userRef)) { it.getValue(User::class.java) }
}