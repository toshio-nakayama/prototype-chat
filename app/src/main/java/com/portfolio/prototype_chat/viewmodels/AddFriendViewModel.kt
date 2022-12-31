package com.portfolio.prototype_chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.networks.SingleValueEventLiveData
import com.portfolio.prototype_chat.utils.NodeNames

class AddFriendViewModel(val userId: String) : ViewModel() {
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS).child(userId)
    
    val userLiveData: LiveData<User> = Transformations.map(SingleValueEventLiveData(userRef)) {
        it.getValue(
            User::class.java)
    }
    
    class Factory(val userId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddFriendViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddFriendViewModel(userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}