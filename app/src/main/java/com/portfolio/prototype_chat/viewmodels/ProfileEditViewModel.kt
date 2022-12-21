package com.portfolio.prototype_chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.networks.ValueEventLiveData
import com.portfolio.prototype_chat.utils.NodeNames

class ProfileEditViewModel : ViewModel(){
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser
    private val rootRef: DatabaseReference = Firebase.database.reference
    private val userRef: DatabaseReference = rootRef.child(NodeNames.USERS).child(currentUser!!.uid)
    private val storageRootRef: StorageReference = Firebase.storage.reference
    val userLiveData: LiveData<User> = Transformations.map(ValueEventLiveData(userRef)){it.getValue(User::class.java)}


}